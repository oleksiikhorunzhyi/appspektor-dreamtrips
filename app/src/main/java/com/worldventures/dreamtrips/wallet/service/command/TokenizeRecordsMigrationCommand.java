package com.worldventures.dreamtrips.wallet.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.ActionType;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.TokenizationAnalyticsLocationCommand;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.TokenizationCardAction;
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

/**
 * Should be executed when user has some non-tokenized bank cards stored in database. [v1.17 -> v1.18]
 */
@CommandAction
public class TokenizeRecordsMigrationCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject NxtInteractor nxtInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
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
            .doOnNext(bankCards -> Observable.from(bankCards).doOnNext(this::sendTokenizationAnalytics))
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
    *
    * @param bankCards - NXT security response that can contain both properly tokenized values and errors.
    * @return - cards that were tokenized without any errors.
    */
   private List<BankCard> handleTokenizeResult(List<NxtBankCard> bankCards) {
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

   private void sendTokenizationAnalytics(NxtBankCard nxtBankCard) {
      analyticsInteractor.walletAnalyticsCommandPipe().send(new TokenizationAnalyticsLocationCommand(
            TokenizationCardAction.from(nxtBankCard, ActionType.RESTORE, true)
      ));
   }

   private void clearOldCardStorage() {
      oldCardListStorage.deleteWalletCardList();
   }

}