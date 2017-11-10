package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.GetFirmwareVersionAction;
import io.techery.mappery.MapperyContext;

import static com.worldventures.dreamtrips.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class FetchFirmwareVersionCommand extends Command<SmartCardFirmware> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject MapperyContext mapperyContext;

   @Override
   protected void run(CommandCallback<SmartCardFirmware> callback) throws Throwable {
      janet.createPipe(GetFirmwareVersionAction.class)
            .createObservableResult(new GetFirmwareVersionAction())
            .map(firmwareVersionAction -> mapperyContext.convert(firmwareVersionAction.version, SmartCardFirmware.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}