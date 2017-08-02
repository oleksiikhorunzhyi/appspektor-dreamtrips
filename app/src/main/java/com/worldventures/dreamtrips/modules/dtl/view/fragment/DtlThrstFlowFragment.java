package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstFlowBundle;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstFlowPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.custom.webview.HttpErrorHandlerWebView;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot.DtlPaymentPath;

import butterknife.InjectView;
import flow.Flow;
import flow.History;
import flow.path.Path;

@Layout(R.layout.fragment_dtl_thrst_webview)
public class DtlThrstFlowFragment extends RxBaseFragmentWithArgs<DtlThrstFlowPresenter, ThrstFlowBundle> implements DtlThrstFlowPresenter.View {

   @InjectView(R.id.web_view) HttpErrorHandlerWebView webView;

   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      ThrstFlowBundle thrstFlowBundle = getArgs();
      String receiptUrl = thrstFlowBundle.getReceiptUrl();
      String token = thrstFlowBundle.getToken();
      String transactionId = thrstFlowBundle.getTransactionId();
      String merchantName = thrstFlowBundle.getMerchant().displayName();
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
      return new DtlThrstFlowPresenter();
   }

   @Override
   public boolean onApiError(ErrorResponse errorResponse) {
      return false;
   }

   @Override
   public void onApiCallFailed() {
   }

   @Override
   public void openThankYouScreen(String merchantName, String totalAmount) {
      goToDtlPaymentPath(true, merchantName, totalAmount);
   }

   @Override
   public void openPaymentFailedScreen(String merchantName, String totalAmount) {
      goToDtlPaymentPath(false, merchantName, totalAmount);
   }

   private void goToDtlPaymentPath(boolean isPaid, String totalAmount, String merchantName) {
      Path path = new DtlPaymentPath(isPaid, totalAmount, merchantName);
      History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
      historyBuilder.push(path);
      Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
   }
}
