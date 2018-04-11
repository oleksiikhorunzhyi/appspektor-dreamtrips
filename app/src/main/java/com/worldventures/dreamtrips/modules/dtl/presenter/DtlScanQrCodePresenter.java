package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.annotation.StringRes;

import com.crashlytics.android.Crashlytics;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.InformView;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.ScanMerchantEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.UploadReceiptInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlEarnPointsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.view.util.DtlApiErrorViewAdapter;
import com.worldventures.dreamtrips.modules.dtl.view.util.ProxyApiErrorView;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action0;

public class DtlScanQrCodePresenter extends Presenter<DtlScanQrCodePresenter.View> {

   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject DtlApiErrorViewAdapter apiErrorViewAdapter;
   @Inject UploadReceiptInteractor uploadReceiptInteractor;
   //
   private final Merchant merchant;

   public DtlScanQrCodePresenter(Merchant merchant) {
      this.merchant = merchant;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      apiErrorViewAdapter.setView(new ProxyApiErrorView(view, view::hideProgress));
      view.setMerchant(merchant);
      transactionInteractor.transactionActionPipe()
            .createObservable(DtlTransactionAction.get(merchant))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlTransactionAction>()
                  .onFail(apiErrorViewAdapter::handleError)
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
            .compose(bindViewToMainComposer())
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
               view.showError(message, this::photoUploadFailed);
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
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlTransactionAction>()
                  .onSuccess(action -> {
                     if (action.getResult() != null) {
                        DtlTransaction dtlTransaction = action.getResult();
                        dtlTransaction = ImmutableDtlTransaction.copyOf(dtlTransaction).withMerchantToken(scannedQr);
                        transactionInteractor.transactionActionPipe()
                              .send(DtlTransactionAction.save(merchant, dtlTransaction));
                        checkReceiptUploading(dtlTransaction);
                     }
                  })
                  .onFail(apiErrorViewAdapter::handleError));
   }

   private void tryLogInvalidQr(String scannedCode) {
      if (scannedCode.matches("^[a-zA-Z0-9]+$")) {
         return;
      }
      Crashlytics.log("Invalid QR code scanned: " + scannedCode);
      Crashlytics.logException(new IllegalArgumentException("Invalid QR code scan detected"));
   }

   public void photoUploadFailed() {
      transactionInteractor.transactionActionPipe()
            .createObservable(DtlTransactionAction.clean(merchant))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlTransactionAction>().onFail(apiErrorViewAdapter::handleError)
                  .onSuccess(action -> {
                     if (action.getResult() != null && view != null) {
                        view.openScanReceipt(action.getResult());
                     }
                  }));
   }

   private void onReceiptUploaded(DtlTransaction dtlTransaction) {
      String receiptUrl = dtlTransaction.getUploadTask().getOriginUrl();
      transactionInteractor.earnPointsActionPipe()
            .send(new DtlEarnPointsAction(merchant, ImmutableDtlTransaction.copyOf(dtlTransaction)
                  .withReceiptPhotoUrl(receiptUrl)));
   }

   private void processTransactionResult(DtlEarnPointsAction action) {
      analyticsInteractor.analyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new ScanMerchantEvent(merchant.asMerchantAttributes(),
                  action.getTransaction().getMerchantToken())));
      view.hideProgress();
      transactionInteractor.transactionActionPipe()
            .send(DtlTransactionAction.save(action.getMerchant(), ImmutableDtlTransaction.copyOf(action.getTransaction())
                  .withDtlTransactionResult(action.getResult())));

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
            .compose(bindViewToMainComposer())
            .subscribe(action -> {
            }, apiErrorViewAdapter::handleError);
   }
   ///////////////////////////////////////////////////////////////////////////
   // Receipt uploading
   ///////////////////////////////////////////////////////////////////////////

   private void checkReceiptUploading(DtlTransaction transaction) {
      uploadReceiptInteractor.uploadReceiptCommandPipe().observeWithReplay()
            .compose(bindViewToMainComposer())
            .take(1)
            .filter(state ->
               transaction.getUploadTask() != null
                     && transaction.getUploadTask().getFilePath().equals(state.action.getUploadTask().getFilePath()))
            .subscribe(state -> {
               switch (state.status) {
                  case START:
                  case PROGRESS:
                     view.showProgress(R.string.dtl_wait_for_receipt);
                     break;
                  case SUCCESS:
                     onReceiptUploaded(transaction);
                     break;
                  case FAIL:
                     receiptUploadError();
                     break;
               }
            });
   }

   private void receiptUploadError() {
      view.photoUploadError();
      view.hideProgress();

      // TODO this was dead code before, but looks like it should be revived here to work
      // Should we leave it or remove it?
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .map(transaction -> ImmutableDtlTransaction.copyOf(transaction).withMerchantToken(null))
            .flatMap(transaction -> transactionInteractor.transactionActionPipe()
                  .createObservableResult(DtlTransactionAction.save(merchant, transaction)))
            .compose(bindViewToMainComposer())
            .subscribe(command -> {
            }, apiErrorViewAdapter::handleError);
   }

   @Override
   public void dropView() {
      super.dropView();
      transactionInteractor.earnPointsActionPipe().clearReplays();
   }

   public interface View extends RxView, InformView {
      void finish();

      void showProgress(@StringRes int titleRes);

      void hideProgress();

      void showError(String message, Action0 action);

      void photoUploadError();

      void setMerchant(Merchant merchant);

      void setCamera();

      void openScanReceipt(DtlTransaction dtlTransaction);
   }
}
