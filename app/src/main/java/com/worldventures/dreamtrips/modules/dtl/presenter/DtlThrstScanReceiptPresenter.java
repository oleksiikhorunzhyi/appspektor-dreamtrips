package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.worldventures.core.modules.picker.command.CopyFileCommand;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.modules.picker.service.PickImageDelegate;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableLocation;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.dtl.analytics.CaptureReceiptEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.UrlTokenAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableUrlTokenActionParams;
import com.worldventures.dreamtrips.modules.dtl.view.util.DtlApiErrorViewAdapter;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class DtlThrstScanReceiptPresenter extends JobPresenter<DtlThrstScanReceiptPresenter.View> {

   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject PickImageDelegate pickImageDelegate;
   @Inject MediaPickerInteractor mediaInteractor;
   @Inject MerchantsInteractor merchantInteractor;
   @Inject DtlApiErrorViewAdapter apiErrorViewAdapter;
   @Inject DtlLocationInteractor locationInteractor;
   private final Merchant merchant;

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

      apiErrorViewAdapter.setView(view);
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
            }, apiErrorViewAdapter::handleError);
   }

   private void checkVerification(DtlTransaction transaction) {
      if (transaction.getUploadTask() != null) {
         view.enableVerification();
      } else {
         view.disableVerification();
      }
   }

   public void openThrstFlow() {
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(Command::getResult)
            .subscribe(dtlTransaction -> locationInteractor.locationSourcePipe()
                  .observeSuccessWithReplay()
                  .take(1)
                  .compose(bindViewIoToMainComposer())
                  .flatMap(command -> merchantInteractor.urlTokenThrstHttpPipe()
                        .createObservable(getUrlTokenAction(command.getResult(), dtlTransaction)))
                  .subscribe(new ActionStateSubscriber<UrlTokenAction>()
                        .onSuccess(this::onThrstSuccess)
                        .onFail(this::onThrstError)));
   }

   @NonNull
   private UrlTokenAction getUrlTokenAction(DtlLocation dtlLocation, DtlTransaction dtlTransaction) {
      return UrlTokenAction.create(merchant.id(),
            ImmutableUrlTokenActionParams.builder()
                  .checkinTime(DateTimeUtils.currentUtcString())
                  .merchantId(merchant.id())
                  .currencyCode(merchant.asMerchantAttributes().defaultCurrency().code())
                  .receiptPhotoUrl(photoUploadingManagerS3.getResultUrl(dtlTransaction.getUploadTask()))
                  .location(ImmutableLocation.builder().coordinates(dtlLocation.provideFormattedLocation()).build())
                  .build());
   }

   private void onThrstError(UrlTokenAction urlTokenAction, Throwable throwable) {
      //TODO error should be handled
   }

   private void onThrstSuccess(UrlTokenAction urlTokenAction) {
      view.openThrstFlow(merchant, urlTokenAction.getResult().thrstInfo().redirectUrl(),
            urlTokenAction.getResult().thrstInfo().token(),
            urlTokenAction.getResult().transaction().transactionId());
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
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> attachPhoto(command.getResult()), e -> Timber.e(e, "Failed to copy file"));
   }

   private void attachPhoto(String filePath) {
      analyticsInteractor.analyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new CaptureReceiptEvent(merchant.asMerchantAttributes())));
      view.attachReceipt(Uri.parse(filePath));

      UploadTask uploadTask = new UploadTask();
      uploadTask.setFilePath(filePath);
      TransferObserver transferObserver = photoUploadingManagerS3.upload(uploadTask);
      uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));

      transactionInteractor.transactionActionPipe()
            .createObservable(DtlTransactionAction.update(merchant, transaction ->
                  ImmutableDtlTransaction.copyOf(transaction).withUploadTask(uploadTask)))
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlTransactionAction>()
                  .onSuccess(action -> checkVerification(action.getResult()))
                  .onFail(apiErrorViewAdapter::handleError));
   }

   public interface View extends RxView, DtlApiErrorViewAdapter.ApiErrorView {

      void hideScanButton();

      void attachReceipt(Uri uri);

      void enableVerification();

      void disableVerification();

      void showProgress();

      void openThrstFlow(Merchant merchant, String dtlTransaction, String token, String transactionId);
   }
}