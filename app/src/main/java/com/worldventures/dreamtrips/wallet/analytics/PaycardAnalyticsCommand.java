package com.worldventures.dreamtrips.wallet.analytics;


import android.text.TextUtils;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardIdCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class PaycardAnalyticsCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final BaseCardDetailsWithDefaultAction cardDetailsWithDefaultAction;
   private final BankCard bankCard;

   public PaycardAnalyticsCommand(BaseCardDetailsWithDefaultAction cardDetailsWithDefaultAction, BankCard bankCard) {
      this.cardDetailsWithDefaultAction = cardDetailsWithDefaultAction;
      this.bankCard = bankCard;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      smartCardInteractor.fetchDefaultCardIdCommandPipe()
            .createObservableResult(FetchDefaultCardIdCommand.fetch(false))
            .flatMap(command -> {
               boolean isDefault = TextUtils.equals(bankCard.id(), command.getResult());
               cardDetailsWithDefaultAction.fillPaycardInfo(bankCard, isDefault);
               return analyticsInteractor.walletAnalyticsCommandPipe()
                     .createObservableResult(new WalletAnalyticsCommand(cardDetailsWithDefaultAction));
            })
            .map(Command::getResult)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
