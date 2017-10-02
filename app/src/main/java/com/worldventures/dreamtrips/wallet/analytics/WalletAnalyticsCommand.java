package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletStorage;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class WalletAnalyticsCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject WalletStorage walletStorage;
   @Inject WalletAnalyticsInteractor analyticsInteractor;

   private final WalletAnalyticsAction walletAnalyticsAction;

   public WalletAnalyticsCommand(WalletAnalyticsAction walletAnalyticsAction) {
      this.walletAnalyticsAction = walletAnalyticsAction;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .subscribe(deviceStateCommand -> {
               walletAnalyticsAction.setSmartCardAction(walletStorage.getSmartCard(),
                     deviceStateCommand.getResult(), walletStorage.getSmartCardFirmware());
               analyticsInteractor.analyticsActionPipe().send(walletAnalyticsAction);
               callback.onSuccess(null);
            }, callback::onFail);
   }
}
