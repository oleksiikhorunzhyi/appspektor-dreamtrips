package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.net.Uri;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.worldventures.dreamtrips.api.dtl.merchants.EstimationHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableEstimationParams;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.ImmediateComposer;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.analytics.CaptureReceiptEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.VerifyAmountEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantByIdAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlScanReceiptPresenter extends JobPresenter<DtlScanReceiptPresenter.View> {

   public static final int REQUESTER_ID = -3;

   @Inject DtlMerchantInteractor merchantInteractor;
   @Inject DtlTransactionInteractor transactionInteractor;
   //
   @State double amount;
   //
   private final String merchantId;
   private DtlMerchant dtlMerchant;

   public DtlScanReceiptPresenter(String merchantId) {
      this.merchantId = merchantId;
   }

   @Override
   public void onInjected() {
      super.onInjected();
      merchantInteractor.merchantByIdPipe()
            .createObservable(new DtlMerchantByIdAction(merchantId))
            .compose(ImmediateComposer.instance())
            .subscribe(new ActionStateSubscriber<DtlMerchantByIdAction>().onFail(apiErrorPresenter::handleActionError)
                  .onSuccess(action -> dtlMerchant = action.getResult()));
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      apiErrorPresenter.setView(view);
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(dtlMerchant))
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
      //
      //
      view.showCurrency(dtlMerchant.getDefaultCurrency());
      //
      bindApiJob();
   }

   public void onAmountChanged(double amount) {
      this.amount = amount;
      checkVerification();
   }

   private void checkVerification() {
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(dtlMerchant))
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
            .send(DtlAnalyticsCommand.create(new VerifyAmountEvent(dtlMerchant, dtlMerchant.getDefaultCurrency()
                  .code(), amount)));
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.update(dtlMerchant, transaction -> ImmutableDtlTransaction.copyOf(transaction)
                  .withBillTotal(amount)))
            .map(DtlTransactionAction::getResult)
            .flatMap(transaction -> transactionInteractor.estimatePointsActionPipe()
                  .createObservableResult(new EstimationHttpAction(dtlMerchant.getId(), ImmutableEstimationParams.builder()
                        .checkinTime(DateTimeUtils.currentUtcString())
                        .billTotal(transaction.getBillTotal())
                        .currencyCode(dtlMerchant.getDefaultCurrency().code())
                        .build())))
            .compose(bindViewIoToMainComposer())
            .subscribe(action -> {
            }, apiErrorPresenter::handleError);
   }

   private void attachDtPoints(Double points) {
      transactionInteractor.transactionActionPipe()
            .createObservable(DtlTransactionAction.update(dtlMerchant, transaction -> ImmutableDtlTransaction.copyOf(transaction)
                  .withPoints(points)))
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlTransactionAction>().onFail(apiErrorPresenter::handleActionError)
                  .onSuccess(action -> view.openVerify(action.getResult())));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Photo picking
   ///////////////////////////////////////////////////////////////////////////

   public void scanReceipt() {
      eventBus.post(new ImagePickRequestEvent(PickImageDelegate.CAPTURE_PICTURE, REQUESTER_ID));
   }

   public void onEvent(ImagePickedEvent event) {
      if (event.getRequesterID() == REQUESTER_ID) {
         view.hideScanButton();
         eventBus.removeStickyEvent(event);
         String fileThumbnail = event.getImages()[0].getFileThumbnail();
         imageSelected(Uri.parse(fileThumbnail).toString());
      }
   }

   private void imageSelected(String filePath) {
      savePhotoIfNeeded(filePath);
   }

   private void savePhotoIfNeeded(String filePath) {
      doRequest(new CopyFileCommand(context, filePath), this::attachPhoto);
   }

   private void attachPhoto(String filePath) {
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new CaptureReceiptEvent(dtlMerchant)));
      view.attachReceipt(Uri.parse(filePath));
      //
      UploadTask uploadTask = new UploadTask();
      uploadTask.setFilePath(filePath);
      TransferObserver transferObserver = photoUploadingManagerS3.upload(uploadTask);
      uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));
      //
      transactionInteractor.transactionActionPipe()
            .createObservable(DtlTransactionAction.update(dtlMerchant, transaction -> ImmutableDtlTransaction.copyOf(transaction)
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
