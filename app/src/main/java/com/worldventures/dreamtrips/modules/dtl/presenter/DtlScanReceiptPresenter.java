package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.net.Uri;

import com.worldventures.core.modules.picker.command.CopyFileCommand;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.modules.picker.service.PickImageDelegate;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.api.dtl.merchants.EstimatePointsHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableEstimationParams;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.InformView;
import com.worldventures.dreamtrips.modules.dtl.analytics.CaptureReceiptEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.VerifyAmountEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.UploadReceiptInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.UploadReceiptCommand;
import com.worldventures.dreamtrips.modules.dtl.view.util.ProxyApiErrorView;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class DtlScanReceiptPresenter extends Presenter<DtlScanReceiptPresenter.View> {

   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject PickImageDelegate pickImageDelegate;
   @Inject MediaPickerInteractor mediaPickerInteractor;
   @Inject DtlScanReceiptErrorAdapter apiErrorViewAdapter;
   @Inject UploadReceiptInteractor uploadReceiptInteractor;
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
      mediaPickerInteractor
            .imageCapturedPipe().observeSuccess()
            .map(Command::getResult)
            .compose(bindViewToMainComposer())
            .subscribe(this::receiptScanned);
      apiErrorViewAdapter.setDialogView(view);
      apiErrorViewAdapter.setView(new ProxyApiErrorView(view, () -> view.hideProgress()));
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .compose(bindViewToMainComposer())
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
            }, apiErrorViewAdapter::handleError);

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
            .compose(bindViewToMainComposer())
            .subscribe(this::checkVerification, apiErrorViewAdapter::handleError);
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
            .takeUntil(state -> state.status == ActionState.Status.SUCCESS)
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<EstimatePointsHttpAction>()
                  .onStart(action -> view.showProgress())
                  .onSuccess(action -> attachDtPoints(action.estimatedPoints().points())));
   }

   public void verify() {
      analyticsInteractor.analyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new VerifyAmountEvent(merchant.asMerchantAttributes(), amount)));
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.update(merchant, transaction -> ImmutableDtlTransaction.copyOf(transaction)
                  .withBillTotal(amount)))
            .map(DtlTransactionAction::getResult)
            .flatMap(transaction -> transactionInteractor.estimatePointsActionPipe()
                  .createObservableResult(new EstimatePointsHttpAction(merchant.id(), ImmutableEstimationParams.builder()
                        .checkinTime(DateTimeUtils.currentUtcString())
                        .billTotal(transaction.getBillTotal())
                        .currencyCode(merchant.asMerchantAttributes().defaultCurrency().code())
                        .build())))
            .compose(bindViewToMainComposer())
            .subscribe(action -> {}, throwable -> apiErrorViewAdapter.showError(throwable));
   }

   private void attachDtPoints(Double points) {
      transactionInteractor.transactionActionPipe()
            .createObservable(DtlTransactionAction.update(merchant, transaction -> ImmutableDtlTransaction.copyOf(transaction)
                  .withPoints(points)))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlTransactionAction>().onFail(apiErrorViewAdapter::handleError)
                  .onSuccess(
                        action -> view.openVerify(action.getResult())
                  )
            );
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
      mediaPickerInteractor.copyFilePipe()
            .createObservableResult(new CopyFileCommand(context, filePath))
            .compose(bindViewToMainComposer())
            .subscribe(command -> attachPhoto(command.getResult()), e -> Timber.e(e, "Failed to copy file"));
   }

   private void attachPhoto(String filePath) {
      analyticsInteractor.analyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new CaptureReceiptEvent(merchant.asMerchantAttributes())));
      view.attachReceipt(Uri.parse(filePath));

      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .flatMap(dtlTransactionAction -> {
               UploadTask uploadTask = new UploadTask();
               uploadTask.setFilePath(filePath);
               UploadReceiptCommand command = new UploadReceiptCommand(merchant,
                     dtlTransactionAction.getResult(), uploadTask);
               return uploadReceiptInteractor.uploadReceiptCommandPipe().createObservable(command);
            })
            .filter(state -> state.status == ActionState.Status.PROGRESS ||
               state.status == ActionState.Status.FAIL)
            .take(1)
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<UploadReceiptCommand>()
               .onProgress((uploadReceiptCommand, progress) -> checkVerification(uploadReceiptCommand.getTransaction()))
               .onFail(this::handleError));
   }

   public interface View extends RxView, InformView {
      void openVerify(DtlTransaction dtlTransaction);

      void hideScanButton();

      void attachReceipt(Uri uri);

      void enableVerification();

      void disableVerification();

      void showProgress();

      void hideProgress();

      void preSetBillAmount(double amount);

      void showCurrency(Currency currency);

      void showErrorDialog(String error);
   }
}
