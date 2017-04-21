package com.worldventures.dreamtrips.wallet.analytics;


import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class WalletAnalyticsCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final WalletAnalyticsAction walletAnalyticsAction;
   private SmartCard smartCard = null;

   public WalletAnalyticsCommand(WalletAnalyticsAction walletAnalyticsAction) {
      this.walletAnalyticsAction = walletAnalyticsAction;
   }

   public WalletAnalyticsCommand(SmartCard smartCard, WalletAnalyticsAction walletAnalyticsAction) {
      this.smartCard = smartCard;
      this.walletAnalyticsAction = walletAnalyticsAction;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      if (smartCard == null) {
         smartCardInteractor.activeSmartCardPipe()
               .createObservableResult(new ActiveSmartCardCommand())
               .map(Command::getResult)
               .subscribe(smartCard -> {
                  walletAnalyticsAction.setSmartCardAction(smartCard);
                  analyticsInteractor.analyticsActionPipe().send(walletAnalyticsAction);
                  callback.onSuccess(null);
               }, e -> {
                  analyticsInteractor.analyticsActionPipe().send(walletAnalyticsAction);
                  callback.onFail(e);
               });
      } else {
         walletAnalyticsAction.setSmartCardAction(smartCard);
         analyticsInteractor.analyticsActionPipe().send(walletAnalyticsAction);
         callback.onSuccess(null);
      }
   }
}
