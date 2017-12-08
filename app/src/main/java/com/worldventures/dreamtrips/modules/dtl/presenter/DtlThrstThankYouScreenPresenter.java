package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstPaymentBundle;

public class DtlThrstThankYouScreenPresenter extends JobPresenter<DtlThrstThankYouScreenPresenter.View> {

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
      view.setChargeMoney(Double.parseDouble(thrstPaymentBundle.getTotalAmount()), thrstPaymentBundle.getSubTotalAmount(),
            thrstPaymentBundle.getTaxAmount(), thrstPaymentBundle.getTipAmount());
      view.setEarnedPoints(Integer.valueOf(thrstPaymentBundle.getEarnedPoints()));
      view.setReceiptURL(thrstPaymentBundle.getReceiptURL());
      view.showDoneButton();
      view.hideBackIcon();
   }

   public void onDoneClick() {
      view.goBack(thrstPaymentBundle.isPaid(), thrstPaymentBundle.getEarnedPoints(), thrstPaymentBundle.getTotalPoints());
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
   }
}
