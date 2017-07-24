package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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
   @InjectView(R.id.page_progress) ProgressBar pageProgress;

   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      ThrstFlowBundle thrstFlowBundle = getArgs();
      String receiptUrl = thrstFlowBundle.getReceiptUrl();
      String merchantName = thrstFlowBundle.getMerchant().displayName();
      ((ComponentActivity) getActivity()).getSupportActionBar().setTitle(merchantName);

      webView.loadUrl("http://dev.thrst.com/#/thrst");
      webView.setPageStateCallbackListener(new HttpErrorHandlerWebView.PageStateCallback() {
         @Override
         public void onPageStarted() {
            pageProgress.setVisibility(View.VISIBLE);
         }

         @Override
         public void onPageFinished() {
            pageProgress.setVisibility(View.INVISIBLE);
         }
      });
      webView.setHttpStatusErrorCallbackListener((url, statusCode) ->
            Toast.makeText(getContext(), "URL:" + url + "\nStatus code=" + statusCode, Toast.LENGTH_SHORT).show()
      );
      webView.setJavascriptCallbackListener(message ->
            getPresenter().onThrstCallback(message)
      );
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
   }

   @Override
   public void onResume() {
      super.onResume();
   }

   @Override
   public void onPause() {
      super.onPause();
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
