package com.worldventures.wallet.service.command;

import com.worldventures.janet.injection.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.RestartDeviceAction;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class RestartSmartCardCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      janet.createPipe(RestartDeviceAction.class)
            .createObservableResult(new RestartDeviceAction())
            .subscribe(action -> callback.onSuccess(null), callback::onFail);
   }
}
