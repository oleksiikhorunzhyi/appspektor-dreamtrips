package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
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
         view.setChargeMoney(thrstPaymentBundle.getTotalAmount());
         view.setSuccessResume();
         view.setShowScreenSuccessMessage();
         view.hideSubThankYouMessage();
      } else {
         view.thankYouFailureText();
         view.setFailurePaymentText();
         view.setPaymentFailureImage();
         view.setChargeMoney(thrstPaymentBundle.getTotalAmount());
         view.setFailureResume();
         view.setShowScreenFailureMessage();
         view.showSubThankYouMessage();
      }
   }

   public void onBackPressed() {
      view.goBack();
   }

   public interface View extends RxView, ApiErrorView {

      void setChargeMoney(String money);

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

      void hideSubThankYouMessage();

      void goBack();
   }
}
