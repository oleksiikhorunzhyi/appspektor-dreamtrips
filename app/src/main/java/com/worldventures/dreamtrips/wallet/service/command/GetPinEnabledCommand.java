package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.CheckPinStatusAction;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class GetPinEnabledCommand extends Command<Boolean> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet walletJanet;
   @Inject SmartCardInteractor smartCardInteractor;

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      smartCardInteractor.checkPinStatusActionPipe()
            .createObservableResult(new CheckPinStatusAction())
            .map(checkPinStatusAction -> isPinEnabled(checkPinStatusAction.pinStatus))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Boolean isPinEnabled(CheckPinStatusAction.PinStatus pinStatus) {
      return pinStatus != CheckPinStatusAction.PinStatus.DISABLED;
   }
}
