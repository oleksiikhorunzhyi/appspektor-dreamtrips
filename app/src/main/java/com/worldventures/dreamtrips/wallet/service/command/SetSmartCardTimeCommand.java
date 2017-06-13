package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.SetCardTimeAction;

@CommandAction
public class SetSmartCardTimeCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      smartCardInteractor.smartCardFirmwarePipe()
            .createObservableResult(SmartCardFirmwareCommand.fetch())
            .map(SmartCardFirmwareCommand::getResult)
            .doOnNext(firmware -> {
               if (SCFirmwareUtils.supportOnCardAnalytics(firmware)) {
                  janet.createPipe(SetCardTimeAction.class)
                        .send(new SetCardTimeAction(System.currentTimeMillis()));
               }
            })
            .map(firmware -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
