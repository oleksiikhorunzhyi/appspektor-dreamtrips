package com.worldventures.wallet.service.command;

import com.worldventures.janet.injection.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.lock.GetLockDeviceStatusAction;
import io.techery.janet.smartcard.action.records.GetClearRecordsDelayAction;
import io.techery.janet.smartcard.action.settings.GetDisableDefaultCardDelayAction;
import io.techery.janet.smartcard.action.settings.GetStealthModeAction;
import rx.Observable;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class FetchCardPropertiesCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.zip(
            janet.createPipe(FetchFirmwareVersionCommand.class)
                  .createObservableResult(new FetchFirmwareVersionCommand()),
            janet.createPipe(GetLockDeviceStatusAction.class)
                  .createObservableResult(new GetLockDeviceStatusAction()),
            janet.createPipe(GetStealthModeAction.class)
                  .createObservableResult(new GetStealthModeAction()),
            janet.createPipe(GetDisableDefaultCardDelayAction.class)
                  .createObservableResult(new GetDisableDefaultCardDelayAction()),
            janet.createPipe(GetClearRecordsDelayAction.class)
                  .createObservableResult(new GetClearRecordsDelayAction()),
            (fetchFirmwareVersionCommand, getLockDeviceStatusAction,
                  getStealthModeAction, getDisableDefaultCardDelayAction, getClearRecordsDelayAction) -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
