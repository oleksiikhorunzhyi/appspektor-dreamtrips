package com.worldventures.dreamtrips.wallet.service.firmware.command;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareRepository;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.model.ImmutableConnectionParams;

import static com.worldventures.dreamtrips.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class ConnectForFirmwareUpdate extends Command<Void> implements InjectableAction {

   @Inject FirmwareRepository firmwareRepository;
   @Inject @Named(JANET_WALLET) Janet janet;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      FirmwareUpdateData firmwareUpdateData = firmwareRepository.getFirmwareUpdateData();
      janet.createPipe(ConnectAction.class)
            .createObservableResult(new ConnectAction(ImmutableConnectionParams.of(
                  Long.parseLong(firmwareUpdateData.smartCardId()))))
            .map(firmwareFacade -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
