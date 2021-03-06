package com.worldventures.wallet.service.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.SetStealthModeAction;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class SetStealthModeCommand extends Command<Boolean> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;

   public final boolean stealthModeEnabled;

   public SetStealthModeCommand(boolean stealthModeEnabled) {
      this.stealthModeEnabled = stealthModeEnabled;
   }

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      smartCardInteractor.deviceStatePipe().send(DeviceStateCommand.Companion.stealthMode(stealthModeEnabled));

      janet.createPipe(SetStealthModeAction.class)
            .createObservableResult(new SetStealthModeAction(stealthModeEnabled))
            .map(smartCard -> stealthModeEnabled)
            .subscribe(callback::onSuccess, throwable -> {
               smartCardInteractor.deviceStatePipe()
                     .send(DeviceStateCommand.Companion.stealthMode(!stealthModeEnabled));
               callback.onFail(throwable);
            });
   }
}
