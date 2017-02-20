package com.worldventures.dreamtrips.wallet.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.nxt.DetokenizeBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.NxtInteractor;
import com.worldventures.dreamtrips.wallet.service.nxt.TokenizeMultipleBankCardsCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCard;
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
import timber.log.Timber;

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

   /**
    * Replace local data with data from the SmartCard.
    * Add local-only data to the SmartCard.
    * Sync default card Id.
    */
   private Observable<Void> sync(SyncBundle bundle) {
      final List<Observable<Void>> operations = new ArrayList<>();

      // All SmartCard cards -> tokenize -> save (override) local storage
      if (!bundle.deviceCards.isEmpty()) {
         operations.add(tokenizeBankCards(Queryable.from(bundle.deviceCards)
               .map(deviceOnlyCard -> ImmutableBankCard.copyOf(deviceOnlyCard).withNumberLastFourDigits(
                     BankCardHelper.obtainLastCardDigits(deviceOnlyCard.number())))
               .toList())
               .map(this::handleTokenizeResult)
               .flatMap(this::saveBankCards));
      }

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
      return operations.isEmpty() ? Observable.just(null) : Queryable.from(operations).fold(Observable::concatWith);
   }

   private Observable<List<NxtBankCard>> tokenizeBankCards(List<? extends BankCard> bankCards) {
      return nxtInteractor.tokenizeMultipleBankCardPipe()
            .createObservableResult(new TokenizeMultipleBankCardsCommand(bankCards))
            .map(Command::getResult);
   }

   private Observable<BankCard> detokenizeBankCard(BankCard bankCard) {
      return nxtInteractor.detokenizeBankCardPipe()
            .createObservableResult(new DetokenizeBankCardCommand(bankCard))
            .map(detokenizeResult -> detokenizeResult.getResult().getDetokenizedBankCard());
   }

   /**
    * Filter out all cards that were not tokenized properly.
    * Send analytic events for any errors that occurred during tokenization.
    *
    * @param bankCards - NXT security response that can contain both properly tokenized values and errors.
    * @return - cards that were tokenized without any errors.
    */
   private List<BankCard> handleTokenizeResult(List<NxtBankCard> bankCards) {
      sendErrorAnalytics(Queryable.from(bankCards)
            .where(bankCard -> !bankCard.getResponseErrors().isEmpty())
            .toList());
      return Queryable.from(bankCards)
            .where(bankCard -> bankCard.getResponseErrors().isEmpty())
            .map(NxtBankCard::getTokenizedBankCard)
            .toList();
   }

   private Observable<Void> saveBankCards(List<BankCard> tokenizedBankCards) {
      return interactor.cardsListPipe()
            .createObservableResult(CardListCommand.replace(tokenizedBankCards))
            .map(o -> null);
   }

   private void sendErrorAnalytics(List<NxtBankCard> errorTokenizeResults) {
      // TODO: 2/24/17 When task is RFD
      Queryable.from(errorTokenizeResults).map(NxtBankCard::getResponseErrors).forEachR(
            errorResponses -> Queryable.from(errorResponses)
                  .map(element -> String.format("[%s: %s]", element.code(), element.message()))
                  .forEachR(errorMessage -> Timber.e("Tokenization error: %s", errorMessage)));
   }

   private static class SyncBundle {
      private List<BankCard> deviceCards;
      private List<BankCard> localCards;
      private String deviceDefaultCardId;
      private String localDefaultCardId;
   }

}