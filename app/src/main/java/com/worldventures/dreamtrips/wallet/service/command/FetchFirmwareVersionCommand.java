package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.GetFirmwareVersionAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class FetchFirmwareVersionCommand extends Command<SmartCardFirmware> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject MapperyContext mapperyContext;

   @Override
   protected void run(CommandCallback<SmartCardFirmware> callback) throws Throwable {
      janet.createPipe(GetFirmwareVersionAction.class)
            .createObservableResult(new GetFirmwareVersionAction())
            .map(firmwareVersionAction -> mapperyContext.convert(firmwareVersionAction.version, SmartCardFirmware.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
