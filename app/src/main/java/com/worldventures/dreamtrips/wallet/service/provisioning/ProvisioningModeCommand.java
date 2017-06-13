package com.worldventures.dreamtrips.wallet.service.provisioning;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public class ProvisioningModeCommand extends Command<ProvisioningMode> implements InjectableAction {

   private final Func1<ProvisioningMode, ProvisioningMode> func;

   @Inject ProvisioningModeStorage provisioningModeStorage;

   private ProvisioningModeCommand(Func1<ProvisioningMode, ProvisioningMode> func) {
      this.func = func;
   }

   public static ProvisioningModeCommand saveState(ProvisioningMode state) {
      return new ProvisioningModeCommand(s -> state);
   }

   public static ProvisioningModeCommand fetchState() {
      return new ProvisioningModeCommand(state -> state);
   }

   public static ProvisioningModeCommand clear() {
      return new ProvisioningModeCommand(state -> null);
   }

   @Override
   protected void run(CommandCallback<ProvisioningMode> callback) throws Throwable {
      ProvisioningMode state = provisioningModeStorage.getState();
      ProvisioningMode newState = func.call(state);
      if (newState != state) {
         provisioningModeStorage.saveState(newState);
      } else if (newState == null) {
         provisioningModeStorage.clear();
      }
      callback.onSuccess(newState);
   }
}
