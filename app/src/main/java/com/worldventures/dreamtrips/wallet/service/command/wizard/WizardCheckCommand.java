package com.worldventures.dreamtrips.wallet.service.command.wizard;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;

import org.immutables.value.Value;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class WizardCheckCommand extends Command<WizardCheckCommand.Checks> implements InjectableAction {

   @Inject WalletBluetoothService bluetoothService;
   @Inject WalletNetworkService networkService;

   @Override
   protected void run(CommandCallback<Checks> callback) throws Throwable {
      callback.onSuccess(ImmutableChecks.builder()
            .bleIsSupported(bluetoothService.isSupported())
            .bluetoothIsEnabled(bluetoothService.isEnable())
            .internetIsAvailable(networkService.isAvailable())
            .build());
   }

   @Value.Immutable
   public interface Checks {

      boolean bleIsSupported();

      boolean bluetoothIsEnabled();

      boolean internetIsAvailable();
   }
}
