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
         view.thankYouSuccessfulText();
         view.setSuccessPaymentText();
         view.setPaymentSuccessImage();
         view.setSuccessResume();
         view.setShowScreenSuccessMessage();
      } else {
         view.thankYouFailureText();
         view.setFailurePaymentText();
         view.setPaymentFailureImage();
         view.setFailureResume();
         view.setShowScreenFailureMessage();
         view.showSubThankYouMessage();
         view.hideViewsOnError();
      }
      view.setChargeMoney(Double.parseDouble(thrstPaymentBundle.getTotalAmount()), 0, 0, 0);
      view.setEarnedPoints(Integer.valueOf(thrstPaymentBundle.getEarnedPoints()));
      view.setReceiptURL(thrstPaymentBundle.getReceiptURL());
      view.showDoneButton();
   }

   public void onDoneClick() {
      view.goBack(thrstPaymentBundle.isPaid(), thrstPaymentBundle.getEarnedPoints(), thrstPaymentBundle.getTotalPoints());
   }

   public interface View extends RxView {

      void setChargeMoney(double money, double subTotal, double taxAmount, double tipAmount);

      void thankYouSuccessfulText();

      void thankYouFailureText();

      void setSuccessPaymentText();

      void setFailurePaymentText();

      void setSuccessResume();

      void setFailureResume();

      void setPaymentSuccessImage();

      void setPaymentFailureImage();

      void setShowScreenSuccessMessage();

      void setShowScreenFailureMessage();

      void showSubThankYouMessage();

      void showDoneButton();

      void hideViewsOnError();

      void setEarnedPoints(int earnedPoints);

      void setReceiptURL(String url);

      void goBack(boolean isPaid, String earnedPoints, String totalPoints);
   }
}
