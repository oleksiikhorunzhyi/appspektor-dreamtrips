package com.worldventures.dreamtrips.modules.dtl.view.custom.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.worldventures.dreamtrips.modules.dtl.view.custom.webview.client.HttpErrorHandlerWebViewClient;
import com.worldventures.dreamtrips.modules.dtl.view.custom.webview.javascript.JavaScriptInterface;

import static com.worldventures.dreamtrips.modules.dtl.view.custom.webview.javascript.JavaScriptInterface.JAVASCRIPT_INTERFACE;

public class HttpErrorHandlerWebView extends WebView {
   private Handler handler = new Handler();
   private HttpStatusErrorCallback httpStatusErrorCallback;
   private JavascriptCallback javascriptCallback;

   public HttpErrorHandlerWebView(Context context) {
      super(context);
      init();
   }

   public HttpErrorHandlerWebView(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   public HttpErrorHandlerWebView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init();
   }

   @TargetApi(VERSION_CODES.LOLLIPOP)
   public HttpErrorHandlerWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      init();
   }

   @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
   private void init() {
      getSettings().setJavaScriptEnabled(true);
      getSettings().setDefaultTextEncodingName("utf-8");
      setWebViewClient(new HttpErrorHandlerWebViewClient() {
         @Override
         protected void onHttpStatusError(final String url, final int statusCode) {
            if (httpStatusErrorCallback != null) {
               handler.post(() -> httpStatusErrorCallback.onHttpStatusError(url, statusCode));
            }
         }
      });
      addJavascriptInterface(new JavaScriptInterface() {
         @Override
         public void thrstCallback(String message) {
            if (javascriptCallback != null) {
               javascriptCallback.onThrstCallback(message);
            }
         }
      }, JAVASCRIPT_INTERFACE);

      setOnKeyListener((view, keyCode, keyEvent) -> {
         if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK && canGoBack()) {
               goBack();
               return true;
            }
         }
         return false;
      });
   }

   public void setHttpStatusErrorCallback(HttpStatusErrorCallback httpStatusErrorCallback) {
      this.httpStatusErrorCallback = httpStatusErrorCallback;
   }

   public void setJavascriptCallback(JavascriptCallback javascriptCallback) {
      this.javascriptCallback = javascriptCallback;
   }

   public interface HttpStatusErrorCallback {
      void onHttpStatusError(String url, int statusCode);
   }

   public interface JavascriptCallback {
      void onThrstCallback(String message);
   }
}
