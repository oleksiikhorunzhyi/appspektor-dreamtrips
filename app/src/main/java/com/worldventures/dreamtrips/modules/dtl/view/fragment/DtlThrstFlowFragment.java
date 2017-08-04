package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstFlowBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstPaymentBundle;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstFlowPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.custom.webview.HttpErrorHandlerWebView;

import butterknife.InjectView;

@Layout(R.layout.fragment_dtl_thrst_webview)
public class DtlThrstFlowFragment extends RxBaseFragmentWithArgs<DtlThrstFlowPresenter, ThrstFlowBundle> implements DtlThrstFlowPresenter.View {

   @InjectView(R.id.web_view) HttpErrorHandlerWebView webView;

   private String merchantName;

   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      ThrstFlowBundle thrstFlowBundle = getArgs();
      String receiptUrl = thrstFlowBundle.getReceiptUrl();
      String token = thrstFlowBundle.getToken();
      merchantName = thrstFlowBundle.getMerchant().displayName();
      ((ComponentActivity) getActivity()).getSupportActionBar().setTitle(merchantName);
      final String cookieString = "\"auth=" + token + "; Domain=.thrst.com; Path=/;\"";

      WebView.setWebContentsDebuggingEnabled(true);
      webView.init();
      webView.setWebChromeClient(new WebChromeClient());
      webView.setWebViewClient(new WebViewClient());
      String executeThrstJs = "<script type='text/javascript'>javascript:(function () { " +
            "document.cookie = " + cookieString + ";\n" +
            "console.log(document.cookie);" +
            "window.location.href = '" + receiptUrl + "';\n" +
            "})()</script>";
      webView.loadDataWithBaseURL(receiptUrl, executeThrstJs, "text/html", "utf-8", null);

      webView.setJavascriptCallbackListener(message ->
            getPresenter().onThrstCallback(message)
      );
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
   }

   @Override
   protected DtlThrstFlowPresenter createPresenter(Bundle savedInstanceState) {
      return new DtlThrstFlowPresenter(getArgs());
   }

   @Override
   public boolean onApiError(ErrorResponse errorResponse) {
      return false;
   }

   @Override
   public void onApiCallFailed() {
   }

   @Override
   public void openThankYouScreen(String totalAmount) {
      goToDtlPaymentPath(true, totalAmount);
   }

   @Override
   public void openPaymentFailedScreen(String totalAmount) {
      goToDtlPaymentPath(false, totalAmount);
   }

   private void goToDtlPaymentPath(boolean isPaid, String totalAmount) {
      router.back();
      router.moveTo(
            Route.DTL_THRST_THANK_YOU_SCREEN,
            NavigationConfigBuilder.forActivity()
                  .data(new ThrstPaymentBundle(getArgs().getMerchant(), isPaid, totalAmount))
                  .build()
      );
   }
}
