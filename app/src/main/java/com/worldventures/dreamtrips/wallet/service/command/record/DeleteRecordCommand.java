package com.worldventures.dreamtrips.wallet.service.command.record;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.DeleteRecordAction;

import static com.worldventures.dreamtrips.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class DeleteRecordCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject RecordInteractor recordInteractor;

   private final String recordId;

   public DeleteRecordCommand(String recordId) {
      this.recordId = recordId;
   }

   public String getRecordId() {
      return recordId;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      janet.createPipe(DeleteRecordAction.class)
            .createObservableResult(new DeleteRecordAction(Integer.valueOf(recordId)))
            .flatMap(aVoid -> recordInteractor.cardsListPipe()
                  .createObservableResult(RecordListCommand.remove(recordId)))
            .map(deleteRecordAction -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
