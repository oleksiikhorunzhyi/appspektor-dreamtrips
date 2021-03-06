package com.worldventures.wallet.service.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.wallet.util.SCFirmwareUtils;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.SetCardTimeAction;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class SetSmartCardTimeCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      smartCardInteractor.smartCardFirmwarePipe()
            .createObservableResult(SmartCardFirmwareCommand.Companion.fetch())
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
