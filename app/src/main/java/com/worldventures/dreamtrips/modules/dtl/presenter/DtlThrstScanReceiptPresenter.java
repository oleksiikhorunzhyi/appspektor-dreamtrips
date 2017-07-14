package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.net.Uri;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.command.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.analytics.CaptureReceiptEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DtlThrstScanReceiptPresenter extends JobPresenter<DtlThrstScanReceiptPresenter.View> {

   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject PickImageDelegate pickImageDelegate;
   @Inject MediaInteractor mediaInteractor;
   private final Merchant merchant;

   private UploadTask uploadTask;

   public DtlThrstScanReceiptPresenter(Merchant merchant) {
      this.merchant = merchant;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      mediaInteractor
            .imageCapturedPipe().observeSuccess()
            .map(Command::getResult)
            .compose(bindViewToMainComposer())
            .subscribe(this::receiptScanned);

      apiErrorPresenter.setView(view);
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(transaction -> {
               if (transaction.getUploadTask() != null) {
                  view.hideScanButton();
                  view.attachReceipt(Uri.parse(transaction.getUploadTask().getFilePath()));
               }
               checkVerification(transaction);
            }, apiErrorPresenter::handleError);
   }

   private void checkVerification(DtlTransaction transaction) {
      if (transaction.getUploadTask() != null) {
         view.enableVerification();
      } else {
         view.disableVerification();
      }
   }

   public void openThrstFlow() {
      view.openThrstFlow(merchant, photoUploadingManagerS3.getResultUrl(uploadTask));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Photo picking
   ///////////////////////////////////////////////////////////////////////////

   public void scanReceipt() {
      pickImageDelegate.takePicture();
   }

   private void receiptScanned(String filePath) {
      view.enableVerification();
      view.hideScanButton();
      String fileThumbnail = "file://" + filePath;
      imageSelected(Uri.parse(fileThumbnail).toString());
   }

   private void imageSelected(String filePath) {
      savePhotoIfNeeded(filePath);
   }

   private void savePhotoIfNeeded(String filePath) {
      mediaInteractor.copyFilePipe()
            .createObservableResult(new CopyFileCommand(context, filePath))
            .compose(bindViewToMainComposer())
            .subscribe(command -> attachPhoto(command.getResult()), e -> Timber.e(e, "Failed to copy file"));
   }

   private void attachPhoto(String filePath) {
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new CaptureReceiptEvent(merchant.asMerchantAttributes())));
      view.attachReceipt(Uri.parse(filePath));

      uploadTask = new UploadTask();
      uploadTask.setFilePath(filePath);
      TransferObserver transferObserver = photoUploadingManagerS3.upload(uploadTask);
      uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));

      transactionInteractor.transactionActionPipe()
            .createObservable(DtlTransactionAction.update(merchant, transaction -> ImmutableDtlTransaction.copyOf(transaction)
                  .withUploadTask(uploadTask)))
            .subscribeOn(Schedulers.io())
            .subscribe(new ActionStateSubscriber<DtlTransactionAction>().onSuccess(action -> checkVerification(action.getResult()))
                  .onFail(apiErrorPresenter::handleActionError));
   }

   public interface View extends RxView, ApiErrorView {

      void hideScanButton();

      void attachReceipt(Uri uri);

      void enableVerification();

      void disableVerification();

      void showProgress();

      void openThrstFlow(Merchant merchant, String dtlTransaction);
   }
}
