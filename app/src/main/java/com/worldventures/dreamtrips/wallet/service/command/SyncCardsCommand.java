package com.worldventures.dreamtrips.wallet.service.command;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
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
   @Inject SnappyRepository snappyRepository;

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
               bundle.deviceDefaultCardId = deviceDefaultCardId > 0 ? String.valueOf(deviceDefaultCardId) : null;
               bundle.cacheDefaultCardId = cacheDefaultCardId;
               return bundle;
            }
      ).flatMap(this::sync)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> sync(SyncBundle bundle) {
      List<Observable<Void>> operations = new ArrayList<>();
      //sync card list
      Queryable.from(bundle.deviceCards)
            .forEachR(deviceCard -> {
               if (Queryable.from(bundle.cacheCards)
                     .count(element -> element.id() != null && element.id().equals(deviceCard.id())) == 0) {
                  operations.add(interactor.cardsListPipe()
                        .createObservableResult(CardListCommand.add(deviceCard))
                        .map(value -> null));
               }
            });

      Queryable.from(bundle.cacheCards)
            .forEachR(cacheCard -> {
               if (Queryable.from(bundle.deviceCards)
                     .count(element -> element.id() != null && element.id().equals(cacheCard.id())) == 0) {
                  operations.add(interactor.addRecordPipe()
                        .createObservableResult(new AttachCardCommand((BankCard) cacheCard, false))
                        .map(value -> null));
               }
            });
      //sync default card id
      if (bundle.deviceDefaultCardId != null && bundle.cacheDefaultCardId == null) {
         operations.add(Observable.fromCallable(() -> {
            snappyRepository.saveWalletDefaultCardId(bundle.deviceDefaultCardId);
            return null;
         }));
      } else if (bundle.cacheDefaultCardId != null && !bundle.cacheDefaultCardId.equals(bundle.deviceDefaultCardId)) {
         operations.add(interactor.setDefaultCardOnDeviceCommandPipe()
               .createObservableResult(SetDefaultCardOnDeviceCommand.setAsDefault(bundle.cacheDefaultCardId))
               .map(value -> null));
      }
      return Queryable.from(operations)
            .fold((observable, observable2) -> observable.concatWith(observable2));
   }


   private static class SyncBundle {
      private List<Card> cacheCards;
      private List<Card> deviceCards;
      private String deviceDefaultCardId;
      private String cacheDefaultCardId;
   }

}
