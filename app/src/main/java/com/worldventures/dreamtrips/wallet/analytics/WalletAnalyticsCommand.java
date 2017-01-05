package com.worldventures.dreamtrips.wallet.analytics;


import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
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

   public WalletAnalyticsCommand(WalletAnalyticsAction walletAnalyticsAction) {
      this.walletAnalyticsAction = walletAnalyticsAction;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
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
   }
}
