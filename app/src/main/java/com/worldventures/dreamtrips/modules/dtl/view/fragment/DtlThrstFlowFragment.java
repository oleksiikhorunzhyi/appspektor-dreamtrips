package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstFlowPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.custom.webview.HttpErrorHandlerWebView;

import butterknife.InjectView;

@Layout(R.layout.fragment_dtl_thrst_webview)
public class DtlThrstFlowFragment extends RxBaseFragment<DtlThrstFlowPresenter> implements DtlThrstFlowPresenter.View {

   private static final String HTML = "<!DOCTYPE html>\n" +
         "<html>\n" +
         "<head>\n" +
         "<script src='http://code.jquery.com/jquery-2.1.1.min.js'></script>\n" +
         "    <meta charset='utf-8'>\n" +
         "    <meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no' />\n" +
         "    <title>javascript callback</title>\n" +
         "    <script type='text/javascript'>\n" +
         "        $(function () {\n" +
         "            var transaction = $('#txButton');\n" +
         "            function call_native () {\n" +
         "                var prop = 'transaction_id';\n" +
         "                window.mobileTHRSTContext.thrstCallback('whatever');\n" +
         "            }\n" +
         "            setTimeout(call_native, 1000);\n" +
         "            transaction.on('click', call_native);\n" +
         "            // Expose that function globally\n" +
         "             window.call_native = call_native;\n" +
         "        });\n" +
         "    </script>\n" +
         "</head>\n" +
         "<body>\n" +
         "    <h2 id='headline'>Test javascript callback</h2>\n" +
         "    <button id='txButton'>Finished transaction</button>\n" +
         "</body>\n" +
         "</html>";

   @InjectView(R.id.web_view) HttpErrorHandlerWebView webView;

   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);

      webView.loadDataWithBaseURL("", HTML, "text/html", "UTF-8", "");
      webView.setHttpStatusErrorCallback((url, statusCode) ->
            Toast.makeText(getContext(), "URL:" + url + "\nStatus code=" + statusCode, Toast.LENGTH_SHORT).show()
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
