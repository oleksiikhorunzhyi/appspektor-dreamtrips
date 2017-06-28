package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.SetPinEnabledAction;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SetPinEnabledCommand extends Command<Boolean> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet walletJanet;
   @Inject SmartCardInteractor smartCardInteractor;

   private final boolean enablePin;

   public SetPinEnabledCommand(boolean enablePin) {
      this.enablePin = enablePin;
   }

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      smartCardInteractor.setPinEnabledActionPipe()
            .createObservableResult(new SetPinEnabledAction(enablePin))
            .map(setPinEnabledAction -> isPinEnabled(setPinEnabledAction.toggleResult))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Boolean isPinEnabled(SetPinEnabledAction.ToggleResult toggleResult) {
      return toggleResult != SetPinEnabledAction.ToggleResult.DISABLED;
   }
}
