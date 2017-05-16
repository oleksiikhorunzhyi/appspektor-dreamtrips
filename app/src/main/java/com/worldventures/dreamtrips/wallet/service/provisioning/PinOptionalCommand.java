package com.worldventures.dreamtrips.wallet.service.provisioning;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

@CommandAction
public class PinOptionalCommand extends Command<Boolean> implements InjectableAction {

   @Inject PinOptionalStorage pinOptionalStorage;
   private final Func1<Boolean, Boolean> func;

   private PinOptionalCommand(Func1<Boolean, Boolean> func) {
      this.func = func;
   }

   public static PinOptionalCommand save(boolean shouldAskForPin) {
      return new PinOptionalCommand(aBoolean -> shouldAskForPin);
   }

   public static PinOptionalCommand fetch() {
      return new PinOptionalCommand(shouldAskForPin -> shouldAskForPin);
   }

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      final Boolean shouldAskForPin = pinOptionalStorage.shouldAskForPin();
      final Boolean shouldAskForPinNewState = func.call(shouldAskForPin);
      if (shouldAskForPin != shouldAskForPinNewState) {
         pinOptionalStorage.saveShouldAskForPin(shouldAskForPinNewState);
      }
      callback.onSuccess(shouldAskForPinNewState);
   }
}
