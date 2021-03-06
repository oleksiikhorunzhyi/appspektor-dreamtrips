package com.worldventures.wallet.service.command;

import com.worldventures.janet.injection.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.SetActiveRecordAction;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;
import static java.lang.Integer.valueOf;

@CommandAction
public class SetPaymentCardAction extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;

   private final String recordId;

   public SetPaymentCardAction(String recordId) {
      this.recordId = recordId;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      janet.createPipe(SetActiveRecordAction.class)
            .createObservableResult(new SetActiveRecordAction(valueOf(recordId), true))
            .subscribe(action -> callback.onSuccess(null), callback::onFail);
   }
}
