package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.content.Context;
import android.support.annotation.StringRes;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.services.cognitoidentity.model.InvalidParameterException;
import com.crashlytics.android.Crashlytics;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.InformView;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.ScanMerchantEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlEarnPointsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.view.util.DtlApiErrorViewAdapter;
import com.worldventures.dreamtrips.modules.dtl.view.util.ProxyApiErrorView;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action0;
import timber.log.Timber;

public class DtlScanQrCodePresenter extends JobPresenter<DtlScanQrCodePresenter.View> implements TransferListener {

   @Inject Context context;
   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject @Global EventBus eventBus;
   @Inject DtlApiErrorViewAdapter apiErrorViewAdapter;
   //
   private final Merchant merchant;
   private TransferObserver transferObserver;

   public DtlScanQrCodePresenter(Merchant merchant) {
      this.merchant = merchant;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      try {
         eventBus.registerSticky(this);
      } catch (Exception ignored) {
         Timber.v("EventBus :: Problem on registering sticky - no \'onEvent' method found in " + getClass().getName());
      }
      apiErrorViewAdapter.setView(new ProxyApiErrorView(view, () -> view.hideProgress()));
      //
      view.setMerchant(merchant);
      //
      transactionInteractor.transactionActionPipe()
            .createObservable(DtlTransactionAction.get(merchant))
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlTransactionAction>()
                  .onFail((action, exception) -> {
                     apiErrorViewAdapter.handleError(action, exception);
                  })
                  .onSuccess(action -> {
                     DtlTransaction transaction = action.getResult();
                     if (transaction != null && transaction.isMerchantCodeScanned()) {
                        checkReceiptUploading(transaction);
                     }
                  }));

      bindApiJob();
   }

   private void bindApiJob() {
      transactionInteractor.earnPointsActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlEarnPointsAction>()
                  .onStart(action -> view.showProgress(R.string.dtl_wait_for_earn))
                  .onFail(this::onEarnError)
                  .onSuccess(this::processTransactionResult));
   }

   private void onEarnError(DtlEarnPointsAction action, Throwable throwable) {
      if (action.errorResponse() != null) {
         String field = action.errorResponse().errors().keySet().iterator().next();
         String message = action.errorResponse().reasonForAny();
         switch (field) {
            case DtlTransaction.BILL_TOTAL:
            case DtlTransaction.RECEIPT_PHOTO_URL:
               view.showError(message, () -> photoUploadFailed());
               break;
            case DtlTransaction.LOCATION:
            case DtlTransaction.CHECKIN:
               view.showError(message, () -> view.finish());
               break;
            case DtlTransaction.MERCHANT_TOKEN:
            default:
               view.showError(message, () -> view.setCamera());
               break;
         }
      }
      apiErrorViewAdapter.handleError(action, throwable);
      cleanTransactionToken();
   }

   public void codeScanned(String scannedQr) {
      tryLogInvalidQr(scannedQr);
      transactionInteractor.transactionActionPipe()
            .createObservable(DtlTransactionAction.get(merchant))
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlTransactionAction>().onFail(apiErrorViewAdapter::handleError)
                  .onSuccess(action -> {
                     if (action.getResult() != null) {
                        DtlTransaction dtlTransaction = action.getResult();
                        dtlTransaction = ImmutableDtlTransaction.copyOf(dtlTransaction).withMerchantToken(scannedQr);
                        transactionInteractor.transactionActionPipe()
                              .send(DtlTransactionAction.save(merchant, dtlTransaction));
                        checkReceiptUploading(dtlTransaction);
                     }
                  }));
   }

   private void tryLogInvalidQr(String scannedCode) {
      if (scannedCode.matches("^[a-zA-Z0-9]+$")) return;
      Crashlytics.log("Invalid QR code scanned: " + scannedCode);
      Crashlytics.logException(new InvalidParameterException("Invalid QR code scan detected"));
   }

   public void photoUploadFailed() {
      transactionInteractor.transactionActionPipe()
            .createObservable(DtlTransactionAction.clean(merchant))
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlTransactionAction>().onFail(apiErrorViewAdapter::handleError)
                  .onSuccess(action -> {
                     if (action.getResult() != null) {
                        if (view != null) view.openScanReceipt(action.getResult());
                     }
                  }));

   }

   private void onReceiptUploaded() {
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(Command::getResult)
            .map(transaction -> ImmutableDtlTransaction.copyOf(transaction)
                  .withReceiptPhotoUrl(photoUploadingManagerS3.getResultUrl(transaction.getUploadTask())))
            .subscribe(dtlTransaction -> transactionInteractor.earnPointsActionPipe()
                  .send(new DtlEarnPointsAction(merchant, dtlTransaction)), apiErrorViewAdapter::handleError);

   }

   @Override
   public void handleError(Object action, Throwable error) {
      super.handleError(action, error);
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .map(transaction -> ImmutableDtlTransaction.copyOf(transaction).withMerchantToken(null))
            .flatMap(transaction -> transactionInteractor.transactionActionPipe()
                  .createObservableResult(DtlTransactionAction.save(merchant, transaction)))
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> {
            }, apiErrorViewAdapter::handleError);
   }

   private void processTransactionResult(DtlEarnPointsAction action) {
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new ScanMerchantEvent(merchant.asMerchantAttributes(),
                  action.getTransaction().getMerchantToken())));
      view.hideProgress();

      transactionInteractor.transactionActionPipe()
            .send(DtlTransactionAction.save(action.getMerchant(), ImmutableDtlTransaction.copyOf(action.getTransaction())
                  .withDtlTransactionResult(action.getResult())));
      ;

      eventBus.postSticky(new DtlTransactionSucceedEvent(action.getTransaction()));
      view.finish();
      transactionInteractor.earnPointsActionPipe().clearReplays();
   }


   private void cleanTransactionToken() {
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .map(transaction -> ImmutableDtlTransaction.copyOf(transaction).withMerchantToken(null))
            .flatMap(transaction -> transactionInteractor.transactionActionPipe()
                  .createObservableResult(DtlTransactionAction.save(merchant, transaction)))
            .compose(bindViewIoToMainComposer())
            .subscribe(action -> {}, apiErrorViewAdapter::handleError);
   }
   ///////////////////////////////////////////////////////////////////////////
   // Receipt uploading
   ///////////////////////////////////////////////////////////////////////////

   private void checkReceiptUploading(DtlTransaction transaction) {
      UploadTask uploadTask = transaction.getUploadTask();
      //
      transferObserver = photoUploadingManagerS3.getTransferById(uploadTask.getAmazonTaskId());
      //
      switch (transferObserver.getState()) {
         case FAILED:
            //restart upload if failed
            transferObserver = photoUploadingManagerS3.upload(transaction.getUploadTask());
            uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));
            setListener();
            break;
         case IN_PROGRESS:
            setListener();
            break;
         case COMPLETED:
            onReceiptUploaded();
            break;
         case WAITING_FOR_NETWORK:
            view.noConnection();
            break;
      }
   }

   private void setListener() {
      transferObserver.setTransferListener(this);
      //
      view.showProgress(R.string.dtl_wait_for_receipt);
   }

   @Override
   public void onStateChanged(int id, TransferState state) {
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .filter(transaction -> Integer.valueOf(transaction.getUploadTask().getAmazonTaskId()) == id)
            .compose(bindViewIoToMainComposer())
            .subscribe(transaction -> {
               switch (state) {
                  case COMPLETED:
                     onReceiptUploaded();
                     break;
                  case FAILED:
                     receiptUploadError();
                     break;
                  case WAITING_FOR_NETWORK:
                     view.noConnection();
               }
            }, apiErrorViewAdapter::handleError);
   }

   @Override
   public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
      //nothing to do here
   }

   @Override
   public void onError(int id, Exception ex) {
      receiptUploadError();
   }

   private void receiptUploadError() {
      view.photoUploadError();
      view.hideProgress();
   }

   @Override
   public void dropView() {
      super.dropView();
      if (eventBus.isRegistered(this)) eventBus.unregister(this);
      transactionInteractor.earnPointsActionPipe().clearReplays();
      if (transferObserver != null) transferObserver.setTransferListener(null);
   }

   public interface View extends RxView, InformView {
      void finish();

      void showProgress(@StringRes int titleRes);

      void hideProgress();

      void showError(String message, Action0 action);

      void photoUploadError();

      void noConnection();

      void setMerchant(Merchant merchant);

      void setCamera();

      void openScanReceipt(DtlTransaction dtlTransaction);
   }
}
