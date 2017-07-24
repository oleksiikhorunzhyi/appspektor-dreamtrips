package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;

public class DtlThrstFlowPresenter extends JobPresenter<DtlThrstFlowPresenter.View> {

   private static final String SUCCESS_MESSAGE = "whatever";

   @Override
   public void takeView(View view) {
      super.takeView(view);
   }

   public void onThrstCallback(String message) {
      if (message.equals(SUCCESS_MESSAGE)) {
      } else {

      }
   }

   public interface View extends RxView, ApiErrorView {
      void openThankYouScreen(String merchantName, String totalAmount);

      void openPaymentFailedScreen(String merchantName, String totalAmount);
   }
}
