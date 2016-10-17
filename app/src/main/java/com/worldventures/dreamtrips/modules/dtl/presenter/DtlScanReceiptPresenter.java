package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.net.Uri;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.worldventures.dreamtrips.api.dtl.merchants.EstimationHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableEstimationParams;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.analytics.CaptureReceiptEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.VerifyAmountEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlScanReceiptPresenter extends JobPresenter<DtlScanReceiptPresenter.View> {

   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject PickImageDelegate pickImageDelegate;
   @Inject MediaInteractor mediaInteractor;
   //
   @State double amount;
   //
   private final Merchant merchant;

   public DtlScanReceiptPresenter(Merchant merchant) {
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
               //
               if (transaction.getBillTotal() != 0d) {
                  view.preSetBillAmount(transaction.getBillTotal());
                  this.amount = transaction.getBillTotal();
               }
               checkVerification(transaction);
            }, apiErrorPresenter::handleError);

      view.showCurrency(merchant.asMerchantAttributes().defaultCurrency());

      bindApiJob();
   }

   public void onAmountChanged(double amount) {
      this.amount = amount;
      checkVerification();
   }

   private void checkVerification() {
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(this::checkVerification, apiErrorPresenter::handleError);
   }

   private void checkVerification(DtlTransaction transaction) {
      if (amount > 0d && transaction.getUploadTask() != null) {
         view.enableVerification();
      } else {
         view.disableVerification();
      }
   }

   private void bindApiJob() {
      transactionInteractor.estimatePointsActionPipe()
            .observe()
            .takeUntil(state -> state.status == ActionState.Status.SUCCESS || state.status == ActionState.Status.FAIL)
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<EstimationHttpAction>().onStart(action -> view.showProgress())
                  .onFail(apiErrorPresenter::handleActionError)
                  .onSuccess(action -> attachDtPoints(action.estimatedPoints().points())));
   }

   public void verify() {
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new VerifyAmountEvent(merchant.asMerchantAttributes(), amount)));
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.update(merchant, transaction -> ImmutableDtlTransaction.copyOf(transaction)
                  .withBillTotal(amount)))
            .map(DtlTransactionAction::getResult)
            .flatMap(transaction -> transactionInteractor.estimatePointsActionPipe()
                  .createObservableResult(new EstimationHttpAction(merchant.id(), ImmutableEstimationParams.builder()
                        .checkinTime(DateTimeUtils.currentUtcString())
                        .billTotal(transaction.getBillTotal())
                        .currencyCode(merchant.asMerchantAttributes().defaultCurrency().code())
                        .build())))
            .compose(bindViewIoToMainComposer())
            .subscribe(action -> {
            }, apiErrorPresenter::handleError);
   }

   private void attachDtPoints(Double points) {
      transactionInteractor.transactionActionPipe()
            .createObservable(DtlTransactionAction.update(merchant, transaction -> ImmutableDtlTransaction.copyOf(transaction)
                  .withPoints(points)))
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlTransactionAction>().onFail(apiErrorPresenter::handleActionError)
                  .onSuccess(action -> view.openVerify(action.getResult())));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Photo picking
   ///////////////////////////////////////////////////////////////////////////

   public void scanReceipt() {
      pickImageDelegate.takePicture();
   }

   private void receiptScanned(String filePath) {
      view.hideScanButton();
      String fileThumbnail = "file://" + filePath;
      imageSelected(Uri.parse(fileThumbnail).toString());
   }

   private void imageSelected(String filePath) {
      savePhotoIfNeeded(filePath);
   }

   private void savePhotoIfNeeded(String filePath) {
      doRequest(new CopyFileCommand(context, filePath), this::attachPhoto);
   }

   private void attachPhoto(String filePath) {
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new CaptureReceiptEvent(merchant.asMerchantAttributes())));
      view.attachReceipt(Uri.parse(filePath));
      //
      UploadTask uploadTask = new UploadTask();
      uploadTask.setFilePath(filePath);
      TransferObserver transferObserver = photoUploadingManagerS3.upload(uploadTask);
      uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));
      //
      transactionInteractor.transactionActionPipe()
            .createObservable(DtlTransactionAction.update(merchant, transaction -> ImmutableDtlTransaction.copyOf(transaction)
                  .withUploadTask(uploadTask)))
            .subscribe(new ActionStateSubscriber<DtlTransactionAction>().onSuccess(action -> checkVerification(action.getResult()))
                  .onFail(apiErrorPresenter::handleActionError));
   }

   public interface View extends RxView, ApiErrorView {
      void openVerify(DtlTransaction dtlTransaction);

      void hideScanButton();

      void attachReceipt(Uri uri);

      void enableVerification();

      void disableVerification();

      void showProgress();

      void preSetBillAmount(double amount);

      void showCurrency(Currency currency);
   }
}
