package com.worldventures.dreamtrips.wallet.analytics;


import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class WalletAnalyticsCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final WalletAnalyticsAction walletAnalyticsAction;

   public WalletAnalyticsCommand(WalletAnalyticsAction walletAnalyticsAction) {
      this.walletAnalyticsAction = walletAnalyticsAction;
   }

   @Deprecated
   public WalletAnalyticsCommand(SmartCard smartCard, WalletAnalyticsAction walletAnalyticsAction) {
      this(walletAnalyticsAction);
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.zip(
            smartCardInteractor.activeSmartCardPipe()
                  .createObservableResult(new ActiveSmartCardCommand()),
            smartCardInteractor.deviceStatePipe()
                  .createObservableResult(DeviceStateCommand.fetch()),
            (smartCardCommand, stateCommand) -> new Pair<>(smartCardCommand.getResult(), stateCommand.getResult())
      )
            .subscribe(cardStatePair -> {
               walletAnalyticsAction.setSmartCardAction(cardStatePair.first, cardStatePair.second);
               analyticsInteractor.analyticsActionPipe().send(walletAnalyticsAction);
               callback.onSuccess(null);
            }, e -> {
               analyticsInteractor.analyticsActionPipe().send(walletAnalyticsAction);
               callback.onFail(e);
            });
   }
}
