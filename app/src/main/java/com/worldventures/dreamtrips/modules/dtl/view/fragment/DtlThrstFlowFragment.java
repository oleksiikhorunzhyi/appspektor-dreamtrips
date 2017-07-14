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

import butterknife.InjectView;

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
            Toast.makeText(getContext(), "Callback message=" + message, Toast.LENGTH_SHORT).show()
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
}
