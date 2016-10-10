package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.lock.LockDeviceAction;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SetLockStateCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;

   private final boolean lock;

   public SetLockStateCommand(boolean lock) {
      this.lock = lock;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      if (!lock) throw new IllegalArgumentException("Unlock is not supported");

      janet.createPipe(LockDeviceAction.class)
            .createObservableResult(new LockDeviceAction(true))
            .subscribe(action -> callback.onSuccess(null), callback::onFail);
   }
}
