package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.graphics.Bitmap;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstPaymentBundle;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.SendEmailAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.TakeScreenshotAction;

import java.io.File;

import javax.annotation.Nullable;
import javax.inject.Inject;

import github.nisrulz.screenshott.ScreenShott;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class DtlThrstThankYouScreenPresenter extends JobPresenter<DtlThrstThankYouScreenPresenter.View> {

   @Inject MerchantsInteractor merchantInteractor;

   private final ThrstPaymentBundle thrstPaymentBundle;

   public DtlThrstThankYouScreenPresenter(ThrstPaymentBundle thrstPaymentBundle) {
      this.thrstPaymentBundle = thrstPaymentBundle;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      init();
   }

   private void init() {
      if (thrstPaymentBundle.isPaid()) {
         view.showTransactionSuccessfulMessage();
      } else {
         view.showTransactionFailedMessage();
      }
      view.hideReviewMerchant();
      view.setChargeMoney(Double.parseDouble(thrstPaymentBundle.getTotalAmount()),
            thrstPaymentBundle.getSubTotalAmount(), thrstPaymentBundle.getTaxAmount(),
            thrstPaymentBundle.getTipAmount());
      view.setEarnedPoints(Integer.valueOf(thrstPaymentBundle.getEarnedPoints()));
      view.setReceiptURL(thrstPaymentBundle.getReceiptURL());
      view.showDoneButton();
      view.hideBackIcon();
   }

   public void onDoneClick() {
      view.goBack(thrstPaymentBundle.isPaid(), thrstPaymentBundle.getEarnedPoints(),
            thrstPaymentBundle.getTotalPoints());
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
      merchantInteractor.sendEmailPipe().createObservable(
            new SendEmailAction(
                  thrstPaymentBundle.getMerchant().id(),
                  thrstPaymentBundle.getTransactionId(),
                  path))
            .compose(bindUntilPauseIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SendEmailAction>()
                  .onStart(sendEmailAction -> view.showLoadingDialog())
                  .onSuccess(sendEmailAction -> view.showSuccessEmailMessage())
                  .onFail((sendEmailAction, throwable) -> view.showErrorEmailMessage())
                  .onFinish(sendEmailAction -> view.hideLoadingDialog()));
   }

   public interface View extends RxView {
      void setChargeMoney(double money, double subTotal, double taxAmount, double tipAmount);

      void showTransactionSuccessfulMessage();

      void showTransactionFailedMessage();

      void showDoneButton();

      void hideReviewMerchant();

      void setEarnedPoints(int earnedPoints);

      void setReceiptURL(String url);

      void goBack(boolean isPaid, String earnedPoints, String totalPoints);

      void hideBackIcon();

      void showSuccessEmailMessage();

      void showErrorEmailMessage();

      void showTransactionButtons();

      void hideTransactionButtons();

      void showLoadingDialog();

      void hideLoadingDialog();
   }
}
