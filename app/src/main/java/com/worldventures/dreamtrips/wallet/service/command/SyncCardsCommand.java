package com.worldventures.dreamtrips.wallet.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.GetDefaultRecordAction;
import io.techery.janet.smartcard.action.records.GetMemberRecordsAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SyncCardsCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor interactor;
   @Inject MapperyContext mapperyContext;
   @Inject @Named(JANET_WALLET) Janet janet;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.zip(
            janet.createPipe(GetMemberRecordsAction.class)
                  .createObservableResult(new GetMemberRecordsAction())
                  .flatMap(action -> Observable.from(action.records)
                        .map(record -> (Card) mapperyContext.convert(record, BankCard.class))
                        .toList()),
            interactor.cardsListPipe()
                  .createObservableResult(CardListCommand.fetch())
                  .map(Command::getResult),
            janet.createPipe(GetDefaultRecordAction.class)
                  .createObservableResult(new GetDefaultRecordAction())
                  .map(getDefaultRecordAction -> getDefaultRecordAction.recordId),
            interactor.defaultCardIdPipe()
                  .createObservableResult(new DefaultCardIdCommand())
                  .map(DefaultCardIdCommand::getResult),
            (deviceCards, cacheCards, deviceDefaultCardId, cacheDefaultCardId) -> {
               SyncBundle bundle = new SyncBundle();
               bundle.cacheCards = cacheCards;
               bundle.deviceCards = deviceCards;
               bundle.deviceDefaultCardId = deviceDefaultCardId >= 0 ? String.valueOf(deviceDefaultCardId) : null;
               bundle.cacheDefaultCardId = cacheDefaultCardId;
               return bundle;
            }
      ).flatMap(this::sync)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> sync(SyncBundle bundle) {
      List<Observable<Void>> operations = new ArrayList<>();
      //sync card list

      final List<Card> localCards = new ArrayList<>();
      for (Card cacheCard : bundle.cacheCards) {
         if (!bundle.deviceCards.contains(cacheCard)) {
            localCards.add(cacheCard);
         }
      }
      operations.add(janet.createPipe(AddListRecordCommand.class)
            .createObservableResult(new AddListRecordCommand(localCards))
            .map(command -> {
               final List<Card> cards = new ArrayList<>(bundle.deviceCards);
               cards.addAll(command.getResult());
               return cards;
            })
            .flatMap(cards -> interactor.cardsListPipe()
                  .createObservableResult(CardListCommand.replace(cards))
                  .map(command -> null))
      );

      //sync default card id
      if (bundle.deviceDefaultCardId != null && bundle.cacheDefaultCardId == null) {
         operations.add(interactor.defaultCardIdPipe()
               .createObservableResult(DefaultCardIdCommand.set(bundle.deviceDefaultCardId))
               .map(command -> null)
         );
      } else if (bundle.cacheDefaultCardId != null && !bundle.cacheDefaultCardId.equals(bundle.deviceDefaultCardId)) {
         operations.add(interactor.setDefaultCardOnDeviceCommandPipe()
               .createObservableResult(SetDefaultCardOnDeviceCommand.setAsDefault(bundle.cacheDefaultCardId))
               .map(value -> null));
      }
      return operations.isEmpty() ? Observable.just(null)
            : Queryable.from(operations).fold((observable, observable2) -> observable.concatWith(observable2));
   }


   private static class SyncBundle {
      private List<Card> cacheCards;
      private List<Card> deviceCards;
      private String deviceDefaultCardId;
      private String cacheDefaultCardId;
   }

}
