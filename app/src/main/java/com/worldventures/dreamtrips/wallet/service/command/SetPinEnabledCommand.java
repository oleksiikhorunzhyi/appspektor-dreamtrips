package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.SetPinEnabledAction;

@CommandAction
public class SetPinEnabledCommand extends Command<Boolean> implements InjectableAction {

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
