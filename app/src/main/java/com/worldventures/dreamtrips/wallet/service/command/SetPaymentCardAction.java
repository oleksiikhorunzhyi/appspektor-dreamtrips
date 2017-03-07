package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.SetActiveRecordAction;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static java.lang.Integer.valueOf;

@CommandAction
public class SetPaymentCardAction extends Command<Record> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;

   private final Record record;

   public SetPaymentCardAction(Record record) {this.record = record;}

   @Override
   protected void run(CommandCallback<Record> callback) throws Throwable {
      janet.createPipe(SetActiveRecordAction.class)
            .createObservableResult(new SetActiveRecordAction(valueOf(record.id()), true))
            .map(action -> record)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
