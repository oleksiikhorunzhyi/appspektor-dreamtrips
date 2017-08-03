package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.view.custom.webview.javascript.JavaScriptInterface;

public class DtlThrstFlowPresenter extends JobPresenter<DtlThrstFlowPresenter.View> {

   private static final String SUCCESS_STATUS = "200";

   @Override
   public void takeView(View view) {
      super.takeView(view);
   }

   public void onThrstCallback(JavaScriptInterface.ThrstStatusResponse thrstStatusResponse) {
      if (thrstStatusResponse.status.equals(SUCCESS_STATUS)) {
         view.openThankYouScreen(thrstStatusResponse.body);
      } else {
         view.openPaymentFailedScreen(thrstStatusResponse.body);
      }
   }

   public interface View extends RxView, ApiErrorView {
      void openThankYouScreen(String totalAmount);

      void openPaymentFailedScreen(String totalAmount);
   }
}
