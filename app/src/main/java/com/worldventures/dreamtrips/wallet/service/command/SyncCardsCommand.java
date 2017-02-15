package com.worldventures.dreamtrips.wallet.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.nxt.DetokenizeBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.NxtInteractor;
import com.worldventures.dreamtrips.wallet.service.nxt.TokenizeBankCardCommand;
import com.worldventures.dreamtrips.wallet.util.BankCardHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.AddRecordAction;
import io.techery.janet.smartcard.action.records.GetDefaultRecordAction;
import io.techery.janet.smartcard.action.records.GetMemberRecordsAction;
import io.techery.janet.smartcard.model.Record;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SyncCardsCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor interactor;
   @Inject NxtInteractor nxtInteractor;
   @Inject MapperyContext mapperyContext;
   @Inject @Named(JANET_WALLET) Janet janet;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.zip(
            janet.createPipe(GetMemberRecordsAction.class)
                  .createObservableResult(new GetMemberRecordsAction())
                  .flatMap(action -> Observable.from(action.records)
                        .map(record -> mapperyContext.convert(record, BankCard.class))
                        .toList()),
            interactor.cardsListPipe()
                  .createObservableResult(CardListCommand.fetch())
                  .flatMap(action -> Observable.from(action.getResult())
                        .map(record -> (BankCard) record)
                        .toList()),
            janet.createPipe(GetDefaultRecordAction.class)
                  .createObservableResult(new GetDefaultRecordAction())
                  .map(getDefaultRecordAction -> getDefaultRecordAction.recordId),
            interactor.defaultCardIdPipe()
                  .createObservableResult(new DefaultCardIdCommand())
                  .map(DefaultCardIdCommand::getResult),
            (deviceCards, localCards, deviceDefaultCardId, localDefaultCardId) -> {
               SyncBundle bundle = new SyncBundle();
               bundle.deviceCards = deviceCards;
               bundle.localCards = localCards;
               bundle.deviceDefaultCardId = deviceDefaultCardId >= 0 ? String.valueOf(deviceDefaultCardId) : null;
               bundle.localDefaultCardId = localDefaultCardId;
               return bundle;
            }
      ).flatMap(this::sync)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> sync(SyncBundle bundle) {
      final List<Observable<Void>> operations = new ArrayList<>();

      // SmartCard only cards -> tokenize -> save to persistent storage
      Queryable.from(bundle.deviceCards)
            .filter(deviceCard -> !bundle.localCards.contains(deviceCard))
            .map(deviceOnlyCard -> ImmutableBankCard.copyOf(deviceOnlyCard)
                  .withNumberLastFourDigits(BankCardHelper.obtainLastCardDigits(deviceOnlyCard.number())))
            .forEachR(deviceOnlyCard -> operations.add(tokenizeBankCard(deviceOnlyCard).flatMap(tokenizedBankCard ->
                  interactor.cardsListPipe()
                        .createObservableResult(CardListCommand.add(tokenizedBankCard))
                        .map(value -> null))));

      // Local only cards -> detokenize -> push to SmartCard
      Queryable.from(bundle.localCards)
            .filter(localCard -> !bundle.deviceCards.contains(localCard))
            .forEachR(localOnlyCard -> operations.add(detokenizeBankCard(localOnlyCard).flatMap(detokenizedBankCard ->
                  interactor.addNativeRecordPipe()
                        .createObservableResult(new AddRecordAction(mapperyContext.convert(detokenizedBankCard, Record.class)))
                        .map(value -> null))));

      // Sync default card id
      if (bundle.deviceDefaultCardId != null && bundle.localDefaultCardId == null) {
         operations.add(interactor.defaultCardIdPipe()
               .createObservableResult(DefaultCardIdCommand.set(bundle.deviceDefaultCardId))
               .map(command -> null)
         );
      } else if (bundle.localDefaultCardId != null && !bundle.localDefaultCardId.equals(bundle.deviceDefaultCardId)) {
         operations.add(interactor.setDefaultCardOnDeviceCommandPipe()
               .createObservableResult(SetDefaultCardOnDeviceCommand.setAsDefault(bundle.localDefaultCardId))
               .map(value -> null));
      }
      return operations.isEmpty() ? Observable.just(null)
            : Queryable.from(operations).fold((observable, observable2) -> observable.concatWith(observable2));
   }

   private Observable<BankCard> tokenizeBankCard(BankCard bankCard) {
      return nxtInteractor.tokenizeBankCardPipe()
            .createObservableResult(new TokenizeBankCardCommand(bankCard))
            .map(tokenizeResult -> tokenizeResult.getResult().getTokenizedBankCard());
   }

   private Observable<BankCard> detokenizeBankCard(BankCard bankCard) {
      return nxtInteractor.detokenizeBankCardPipe()
            .createObservableResult(new DetokenizeBankCardCommand(bankCard))
            .map(detokenizeResult -> detokenizeResult.getResult().getDetokenizedBankCard());
   }

   private static class SyncBundle {
      private List<BankCard> deviceCards;
      private List<BankCard> localCards;
      private String deviceDefaultCardId;
      private String localDefaultCardId;
   }

}