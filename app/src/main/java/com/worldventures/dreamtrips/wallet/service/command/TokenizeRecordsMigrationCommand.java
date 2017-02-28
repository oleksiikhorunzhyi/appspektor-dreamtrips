package com.worldventures.dreamtrips.wallet.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.CardListStorage;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.nxt.NxtInteractor;
import com.worldventures.dreamtrips.wallet.service.nxt.TokenizeMultipleBankCardsCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCard;
import com.worldventures.dreamtrips.wallet.util.BankCardHelper;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import timber.log.Timber;

/**
 * Should be executed when user has some non-tokenized bank cards stored in database. [v1.17 -> v1.18]
 */
@CommandAction
public class TokenizeRecordsMigrationCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject NxtInteractor nxtInteractor;
   @Inject CardListStorage oldCardListStorage;

   private int migrateCardsCount;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      final List<BankCard> cardsToMigrate = Queryable.from(oldCardListStorage.readWalletCardsList())
            .cast(BankCard.class)
            .toList();
      if (cardsToMigrate.isEmpty()) {
         callback.onSuccess(null);
         return;
      }
      migrateCardsCount = cardsToMigrate.size();

      tokenizeBankCards(Queryable.from(cardsToMigrate)
            .map(deviceOnlyCard -> ImmutableBankCard.copyOf(deviceOnlyCard).withNumberLastFourDigits(
                  BankCardHelper.obtainLastCardDigits(deviceOnlyCard.number())))
            .toList())
            .map(this::handleTokenizeResult)
            .flatMap(this::saveBankCards)
            .doOnNext(aVoid -> clearOldCardStorage())
            .doOnSubscribe(() -> callback.onProgress(0))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public int getMigrateCardsCount() {
      return migrateCardsCount;
   }

   private Observable<List<NxtBankCard>> tokenizeBankCards(List<? extends BankCard> bankCards) {
      return nxtInteractor.tokenizeMultipleBankCardPipe()
            .createObservableResult(new TokenizeMultipleBankCardsCommand(bankCards))
            .map(Command::getResult);
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
      return smartCardInteractor.cardsListPipe()
            .createObservableResult(CardListCommand.addAll(tokenizedBankCards))
            .map(o -> null);
   }

   private void sendErrorAnalytics(List<NxtBankCard> errorTokenizeResults) {
      // TODO: 2/24/17 When task is RFD
      Queryable.from(errorTokenizeResults).map(NxtBankCard::getResponseErrors).forEachR(
            errorResponses -> Queryable.from(errorResponses)
                  .map(element -> String.format("[%s: %s]", element.code(), element.message()))
                  .forEachR(errorMessage -> Timber.e("Tokenization error: %s", errorMessage)));
   }

   private void clearOldCardStorage() {
      oldCardListStorage.deleteWalletCardList();
   }

}