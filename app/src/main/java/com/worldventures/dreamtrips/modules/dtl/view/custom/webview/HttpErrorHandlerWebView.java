package com.worldventures.dreamtrips.modules.dtl.view.custom.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.worldventures.dreamtrips.modules.dtl.view.custom.webview.client.HttpErrorHandlerWebViewClient;
import com.worldventures.dreamtrips.modules.dtl.view.custom.webview.javascript.JavaScriptInterface;

import static com.worldventures.dreamtrips.modules.dtl.view.custom.webview.javascript.JavaScriptInterface.JAVASCRIPT_INTERFACE;

public class HttpErrorHandlerWebView extends WebView {
   private Handler handler = new Handler();
   private HttpStatusErrorCallback httpStatusErrorCallback;
   private JavascriptCallback javascriptCallback;
   private PageStateCallback pageStateCallback;
   private String thrstToken;

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
      setLayerType(Build.VERSION.SDK_INT >= 19 ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_SOFTWARE, null);
      getSettings().setJavaScriptEnabled(true);
      getSettings().setDefaultTextEncodingName("utf-8");
      setWebChromeClient(new WebChromeClient());
      setWebViewClient(new WebViewClient() {
         @Override
         public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (pageStateCallback != null) {
               pageStateCallback.onPageStarted();
            }
         }

         @Override
         public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (pageStateCallback != null) {
               pageStateCallback.onPageFinished();
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

   public void setHttpStatusErrorCallbackListener(HttpStatusErrorCallback httpStatusErrorCallback) {
      this.httpStatusErrorCallback = httpStatusErrorCallback;
   }

   public void setJavascriptCallbackListener(JavascriptCallback javascriptCallback) {
      this.javascriptCallback = javascriptCallback;
   }

   public void setPageStateCallbackListener(PageStateCallback pageStateCallback) {
      this.pageStateCallback = pageStateCallback;
   }

   public interface HttpStatusErrorCallback {
      void onHttpStatusError(String url, int statusCode);
   }

   public interface JavascriptCallback {
      void onThrstCallback(String message);
   }

   public interface PageStateCallback {
      void onPageStarted();
      void onPageFinished();
   }

   public void setThrstToken(String thrstToken) {
      this.thrstToken = thrstToken;
      init();
   }

   public String getThrstToken() {
      return thrstToken;
   }
}
