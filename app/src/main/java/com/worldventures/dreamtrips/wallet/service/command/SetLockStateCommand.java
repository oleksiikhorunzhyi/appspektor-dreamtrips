package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;

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
   @Inject SmartCardInteractor smartCardInteractor;

   private final boolean lock;

   public SetLockStateCommand(boolean lock) {
      this.lock = lock;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      smartCardInteractor.deviceStatePipe().send(DeviceStateCommand.lock(lock));
      janet.createPipe(LockDeviceAction.class)
            .createObservableResult(new LockDeviceAction(lock))
            .subscribe(action -> callback.onSuccess(null), throwable -> {
               smartCardInteractor.deviceStatePipe().send(DeviceStateCommand.lock(!lock));
               callback.onFail(throwable);
            });
   }

   public boolean isLock() {
      return lock;
   }
}
