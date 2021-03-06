package com.worldventures.wallet.service.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.service.SmartCardInteractor;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.SetClearRecordsDelayAction;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class SetAutoClearSmartCardDelayCommand extends Command<Long> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;

   private final long delayMins;

   public SetAutoClearSmartCardDelayCommand(long delayMins) {
      this.delayMins = delayMins;
   }

   @Override
   protected void run(CommandCallback<Long> callback) throws Throwable {
      janet.createPipe(SetClearRecordsDelayAction.class)
            .createObservableResult(new SetClearRecordsDelayAction(TimeUnit.MINUTES, delayMins))
            .map(action -> action.delay)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
