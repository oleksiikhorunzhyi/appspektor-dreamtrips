package com.worldventures.wallet.service.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.service.SmartCardInteractor;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.GetBatteryLevelAction;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class FetchBatteryLevelCommand extends Command<Integer> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;

   @Override
   protected void run(CommandCallback<Integer> callback) throws Throwable {
      janet.createPipe(GetBatteryLevelAction.class)
            .createObservableResult(new GetBatteryLevelAction())
            .map(action -> Integer.parseInt(action.level))
            .onErrorReturn(throwable -> 0)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
