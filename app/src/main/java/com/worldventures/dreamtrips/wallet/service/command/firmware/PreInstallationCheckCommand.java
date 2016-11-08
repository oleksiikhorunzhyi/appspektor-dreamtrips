package com.worldventures.dreamtrips.wallet.service.command.firmware;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;

import org.immutables.value.Value;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class PreInstallationCheckCommand extends Command<PreInstallationCheckCommand.Checks> implements InjectableAction {

   private final static int MIN_BATTERY_LEVEL = 50;

   @Inject WalletBluetoothService bluetoothService;
   @Inject SmartCardInteractor smartCardInteractor;

   @Override
   protected void run(CommandCallback<PreInstallationCheckCommand.Checks> callback) throws Throwable {
      smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand())
            .map(command -> check(command.getResult()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Checks check(SmartCard smartCard) {
      boolean bluetoothEnabled = bluetoothService.isEnable();
      boolean smartCardConnected = bluetoothEnabled &&
            (smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED || smartCard.connectionStatus() == SmartCard.ConnectionStatus.DFU);
      boolean smartCardCharged = smartCardConnected && smartCard.batteryLevel() >= MIN_BATTERY_LEVEL;
      return ImmutableChecks.builder()
            .bluetoothIsEnabled(bluetoothEnabled)
            .smartCardIsCharged(smartCardCharged)
            .smartCardIsConnected(smartCardConnected)
            .build();
   }

   @Value.Immutable
   public interface Checks {

      boolean bluetoothIsEnabled();

      boolean smartCardIsConnected();

      boolean smartCardIsCharged();
   }
}
