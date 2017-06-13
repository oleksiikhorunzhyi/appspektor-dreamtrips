package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.GetBatteryLevelAction;

@CommandAction
public class FetchBatteryLevelCommand extends Command<Integer> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
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
