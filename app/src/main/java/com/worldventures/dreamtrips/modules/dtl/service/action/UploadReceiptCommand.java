package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.janet.injection.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class UploadReceiptCommand extends Command<DtlTransaction> implements InjectableAction {

   @Inject Janet janet;
   @Inject DtlTransactionInteractor transactionInteractor;
   private Merchant merchant;
   private DtlTransaction transaction;
   private UploadTask uploadTask;

   public UploadReceiptCommand(Merchant merchant, DtlTransaction transaction, UploadTask uploadTask) {
      this.merchant = merchant;
      this.transaction = transaction;
      this.uploadTask = uploadTask;
   }

   @Override
   protected void run(CommandCallback<DtlTransaction> commandCallback) throws Throwable {
      updateTransactionInStorage()
            .doOnNext(action -> checkIfCanceled())
            .doOnNext(dtlTransactionAction -> commandCallback.onProgress(0))
            .flatMap(dtlTransactionAction -> janet.createPipe(SimpleUploaderyCommand.class)
                     .createObservableResult(new SimpleUploaderyCommand(uploadTask.getFilePath())))
            .map(simpleUploaderyCommand -> {
               String url = simpleUploaderyCommand.getResult().response().uploaderyPhoto().location();
               uploadTask.setOriginUrl(url);
               return uploadTask;
            })
            .doOnNext(uploadTask -> checkIfCanceled())
            .flatMap(uploadTask -> updateTransactionInStorage())
            .map(DtlTransactionAction::getResult)
            .subscribe(commandCallback::onSuccess, throwable -> {
               if (!(throwable instanceof RuntimeCancelException)) {
                  commandCallback.onFail(throwable);
               }
            });
   }

   private Observable<DtlTransactionAction> updateTransactionInStorage() {
      return transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.update(merchant,
                  transaction -> ImmutableDtlTransaction.copyOf(transaction)
                  .withUploadTask(uploadTask))).
            doOnNext(dtlTransactionAction -> transaction = dtlTransactionAction.getResult());
   }

   private void checkIfCanceled() {
      if (isCanceled()) {
         throw new RuntimeCancelException();
      }
   }

   @Override
   protected void cancel() {
      super.cancel();
      setCanceled(true);
   }

   public DtlTransaction getTransaction() {
      return transaction;
   }

   public UploadTask getUploadTask() {
      return uploadTask;
   }

   private class RuntimeCancelException extends RuntimeException {
   }
}
