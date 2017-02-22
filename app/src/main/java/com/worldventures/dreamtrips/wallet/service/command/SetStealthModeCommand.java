package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.SetStealthModeAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SetStealthModeCommand extends Command<Boolean> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;

   public final boolean stealthModeEnabled;

   public SetStealthModeCommand(boolean stealthModeEnabled) {
      this.stealthModeEnabled = stealthModeEnabled;
   }

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      fetchSmartCardStatus()
            .flatMap(smartCardStatus -> {
               if (smartCardStatus.stealthMode() == stealthModeEnabled) {
                  return Observable.error(new IllegalArgumentException("Stealth mode already turned " + (stealthModeEnabled ? "on" : "off")));
               } else {
                  return Observable.just(smartCardStatus);
               }
            })
            .flatMap(smartCardStatus -> janet.createPipe(SetStealthModeAction.class)
                  .createObservableResult(new SetStealthModeAction(stealthModeEnabled))
            )
            .map(smartCard -> stealthModeEnabled)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<SmartCardStatus> fetchSmartCardStatus() {
      return janet.createPipe(DeviceStateCommand.class)
            .createObservableResult(DeviceStateCommand.fetch())
            .map(Command::getResult);
   }
}
