package com.worldventures.dreamtrips.wallet.service.firmware.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.firmware.SCFirmwareFacade;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.model.ImmutableConnectionParams;

@CommandAction
public class ConnectForFirmwareUpdate extends Command<Void> implements InjectableAction {

   @Inject SCFirmwareFacade firmwareFacade;
   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      firmwareFacade.takeFirmwareInfo()
            .flatMap(firmwareUpdateData -> janet.createPipe(ConnectAction.class)
                  .createObservableResult(new ConnectAction(ImmutableConnectionParams.of(
                        Integer.parseInt(firmwareUpdateData.smartCardId())))))
            .map(firmwareFacade -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
