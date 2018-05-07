package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.GetTransactionResponse;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.SendEmailAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.TakeScreenshotAction;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class DtlThrstThankYouScreenPresenter extends Presenter<DtlThrstThankYouScreenPresenter.View> {

   @Inject MerchantsInteractor merchantInteractor;
   @Inject DtlTransactionInteractor dtlTransactionInteractor;

   private final Merchant merchant;

   public DtlThrstThankYouScreenPresenter(Merchant merchant) {
      this.merchant = merchant;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      dtlTransactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .compose(bindViewToMainComposer())
            .subscribe(dtlTransactionAction -> init(dtlTransactionAction.getResult()));
   }

   private void init(DtlTransaction dtlTransaction) {
      GetTransactionResponse response = dtlTransaction.getTransactionResponse();
      if (response != null) {
         boolean isPaid = response.transactionStatus().contains(GetTransactionResponse.TRANSACTION_SUCCESSFUL);
         if (isPaid) {
            view.showTransactionSuccessfulMessage();
         } else {
            view.showTransactionFailedMessage();
         }
         view.hideReviewMerchant();
         view.setChargeMoney(Double.parseDouble(response.billTotal()), response.subTotal(), response.tax(), response.tip());
         view.setEarnedPoints(Integer.valueOf(response.pointsAmount()));
         view.setReceiptURL(response.billImagePath());
         view.showDoneButton();
         view.hideBackIcon();
      }
   }

   public void onDoneClick() {
      view.goBack();
   }

   public void onSendEmailClick(android.view.View screenshotView) {
      merchantInteractor.takeScreenshotPipe()
            .createObservable(new TakeScreenshotAction(screenshotView))
            .compose(bindUntilPauseIoToMainComposer())
            .filter(takeScreenshotActionState -> takeScreenshotActionState.action.getResult() != null)
            .subscribe(new ActionStateSubscriber<TakeScreenshotAction>()
                  .onStart(takeScreenshotAction -> view.hideTransactionButtons())
                  .onSuccess(takeScreenshotAction -> sendEmail(takeScreenshotAction.getResult()))
                  .onFinish(takeScreenshotAction -> view.showTransactionButtons()));
   }

   private void sendEmail(String path) {
      dtlTransactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .compose(bindViewToMainComposer())
            .subscribe(dtlTransactionAction ->
                  merchantInteractor.sendEmailPipe()
                        .createObservable(new SendEmailAction(merchant.id(),
                              dtlTransactionAction.getResult().getTransactionResponse().transactionId(), path))
                        .compose(bindUntilPauseIoToMainComposer())
                        .subscribe(new ActionStateSubscriber<SendEmailAction>()
                              .onStart(sendEmailAction -> view.showLoadingDialog())
                              .onSuccess(sendEmailAction -> view.showSuccessEmailMessage())
                              .onFail((sendEmailAction, throwable) -> view.showErrorEmailMessage())
                              .onFinish(sendEmailAction -> view.hideLoadingDialog())));
   }

   public interface View extends RxView {
      void setChargeMoney(double money, double subTotal, double taxAmount, double tipAmount);

      void showTransactionSuccessfulMessage();

      void showTransactionFailedMessage();

      void showDoneButton();

      void hideReviewMerchant();

      void setEarnedPoints(int earnedPoints);

      void setReceiptURL(String url);

      void goBack();

      void hideBackIcon();

      void showSuccessEmailMessage();

      void showErrorEmailMessage();

      void showTransactionButtons();

      void hideTransactionButtons();

      void showLoadingDialog();

      void hideLoadingDialog();
   }
}
