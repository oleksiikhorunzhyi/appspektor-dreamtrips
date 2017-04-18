package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.SetStealthModeAction;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

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
      smartCardInteractor.deviceStatePipe().send(DeviceStateCommand.stealthMode(stealthModeEnabled));

      janet.createPipe(SetStealthModeAction.class)
            .createObservableResult(new SetStealthModeAction(stealthModeEnabled))
            .map(smartCard -> stealthModeEnabled)
            .subscribe(callback::onSuccess, throwable -> {
               smartCardInteractor.deviceStatePipe().send(DeviceStateCommand.stealthMode(!stealthModeEnabled));
               callback.onFail(throwable);
            });
   }
}
