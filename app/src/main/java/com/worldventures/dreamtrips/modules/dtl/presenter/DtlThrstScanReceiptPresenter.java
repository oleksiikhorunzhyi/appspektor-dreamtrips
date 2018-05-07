package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.worldventures.core.modules.picker.command.CopyFileCommand;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.modules.picker.service.PickImageDelegate;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableLocation;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.analytics.CaptureReceiptEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.UploadReceiptInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.UploadReceiptCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.UrlTokenAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableUrlTokenActionParams;
import com.worldventures.dreamtrips.modules.dtl.view.util.DtlApiErrorViewAdapter;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class DtlThrstScanReceiptPresenter extends Presenter<DtlThrstScanReceiptPresenter.View> {

   @Inject Janet janet;
   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject PickImageDelegate pickImageDelegate;
   @Inject MediaPickerInteractor mediaInteractor;
   @Inject MerchantsInteractor merchantInteractor;
   @Inject DtlApiErrorViewAdapter apiErrorViewAdapter;
   @Inject DtlLocationInteractor locationInteractor;
   @Inject UploadReceiptInteractor uploadReceiptInteractor;
   private final Merchant merchant;

   public DtlThrstScanReceiptPresenter(Merchant merchant) {
      this.merchant = merchant;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      mediaInteractor.imageCapturedPipe().observeSuccess()
            .map(Command::getResult)
            .compose(bindViewToMainComposer())
            .subscribe(this::receiptScanned);

      apiErrorViewAdapter.setView(view);
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .compose(bindViewToMainComposer())
            .subscribe(transaction -> {
               if (transaction.getUploadTask() != null) {
                  view.hideScanButton();
                  view.attachReceipt(Uri.parse(transaction.getUploadTask().getFilePath()));
                  view.enableVerification();
               } else {
                  view.disableVerification();
               }
            }, apiErrorViewAdapter::handleError);
   }

   public void openThrstFlow() {
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .compose(bindViewToMainComposer())
            .map(Command::getResult)
            .subscribe(dtlTransaction -> {
               locationInteractor.locationSourcePipe()
                     .observeSuccessWithReplay()
                     .take(1)
                     .flatMap(command -> merchantInteractor.urlTokenThrstHttpPipe()
                           .createObservable(getUrlTokenAction(command.getResult(), dtlTransaction)))
                     .compose(bindViewToMainComposer())
                     .subscribe(new ActionStateSubscriber<UrlTokenAction>()
                           .onStart(urlTokenAction -> view.showProgress())
                           .onSuccess(this::onThrstSuccess)
                           .onFail(this::onThrstError)
                           .onFinish(urlTokenAction -> view.hideProgress()));
            });
   }

   @NonNull
   private UrlTokenAction getUrlTokenAction(DtlLocation dtlLocation, DtlTransaction dtlTransaction) {
      return UrlTokenAction.create(merchant.id(),
            ImmutableUrlTokenActionParams.builder()
                  .checkinTime(DateTimeUtils.currentUtcString())
                  .merchantId(merchant.id())
                  .currencyCode(merchant.asMerchantAttributes().defaultCurrency().code())
                  .receiptPhotoUrl(dtlTransaction.getUploadTask().getOriginUrl())
                  .location(ImmutableLocation.builder().coordinates(dtlLocation.provideFormattedLocation()).build())
                  .build());
   }

   private void onThrstError(UrlTokenAction urlTokenAction, Throwable throwable) {
      view.showThrstOpeningError();
      Timber.e(throwable, "Error opening THRST web-view");
   }

   private void onThrstSuccess(UrlTokenAction urlTokenAction) {
      transactionInteractor.transactionActionPipe()
            .send(DtlTransactionAction.update(merchant, dtlTransaction ->
                  ImmutableDtlTransaction.copyOf(dtlTransaction)
                        .withUrlTokenResponse(urlTokenAction.getResult())));

         view.openThrstFlow(merchant);
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
      mediaInteractor.copyFilePipe()
            .createObservableResult(new CopyFileCommand(context, filePath))
            .compose(bindViewToMainComposer())
            .subscribe(command -> attachPhoto(command.getResult()), e -> Timber.e(e, "Failed to copy file"));
   }

   private void attachPhoto(String filePath) {
      analyticsInteractor.analyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new CaptureReceiptEvent(merchant.asMerchantAttributes())));
      view.attachReceipt(Uri.parse(filePath));
      janet.createPipe(SimpleUploaderyCommand.class)
            .createObservable(new SimpleUploaderyCommand(filePath))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<SimpleUploaderyCommand>()
                  .onStart(simpleUploaderyCommand -> view.showProgress())
                  .onSuccess(simpleUploaderyCommand -> {
                     UploadTask uploadTask = new UploadTask();
                     String url = simpleUploaderyCommand.getResult().response().uploaderyPhoto().location();
                     uploadTask.setOriginUrl(url);
                     uploadTask.setFilePath(filePath);

                     transactionInteractor.transactionActionPipe()
                           .createObservableResult(DtlTransactionAction.update(merchant,
                                 transaction -> ImmutableDtlTransaction.copyOf(transaction)
                                       .withUploadTask(uploadTask)))
                           .compose(bindViewToMainComposer())
                           .subscribe(dtlTransactionAction -> {
                              view.hideProgress();
                              view.enableVerification();
                           });
                  })
                  .onFail((simpleUploaderyCommand, throwable) -> {
                     view.hideProgress();
                     view.showReceiptLoadingError(filePath);
                     Timber.e(throwable, "Loading receipt failed");
                  })
            );
   }

   public void retryPhotoUpload(String filePath) {
      attachPhoto(filePath);
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
                  .onProgress((uploadReceiptCommand, progress) -> view.enableVerification())
                  .onFail(this::handleError));
   }

   public interface View extends RxView, DtlApiErrorViewAdapter.ApiErrorView {

      void hideScanButton();

      void attachReceipt(Uri uri);

      void enableVerification();

      void disableVerification();

      void showProgress();

      void hideProgress();

      void showReceiptLoadingError(String filePath);

      void showThrstOpeningError();

      void openThrstFlow(Merchant merchant);
   }
}
