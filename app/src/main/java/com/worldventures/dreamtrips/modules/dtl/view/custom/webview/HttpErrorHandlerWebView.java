package com.worldventures.dreamtrips.modules.dtl.view.custom.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.worldventures.dreamtrips.modules.dtl.view.custom.webview.javascript.JavaScriptInterface;

import static com.worldventures.dreamtrips.modules.dtl.view.custom.webview.javascript.JavaScriptInterface.JAVASCRIPT_INTERFACE;

public class HttpErrorHandlerWebView extends WebView {
   private JavascriptCallback javascriptCallback;

   public HttpErrorHandlerWebView(Context context) {
      super(context);
   }

   public HttpErrorHandlerWebView(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public HttpErrorHandlerWebView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   @TargetApi(VERSION_CODES.LOLLIPOP)
   public HttpErrorHandlerWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
   }

   @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
   public void init() {
      clearCache(true);
      getSettings().setJavaScriptEnabled(true);
      getSettings().setDefaultTextEncodingName("utf-8");
      getSettings().setAllowUniversalAccessFromFileURLs(true);
      getSettings().setAllowFileAccessFromFileURLs(true);
      getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
      getSettings().setLoadWithOverviewMode(true);
      getSettings().setDatabaseEnabled(true);
      getSettings().setDomStorageEnabled(true);
      getSettings().setAllowContentAccess(true);
      getSettings().setAppCacheEnabled(false);
      getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

      setWebChromeClient(new WebChromeClient());
      setWebViewClient(new WebViewClient());
      addJavascriptInterface(new JavaScriptInterface() {
         @Override
         public void thrstCallback(ThrstStatusResponse thrstStatusResponse) {
            if (javascriptCallback != null) {
               javascriptCallback.onThrstCallback(thrstStatusResponse);
            }
         }
      }, JAVASCRIPT_INTERFACE);
   }

   public void setJavascriptCallbackListener(JavascriptCallback javascriptCallback) {
      this.javascriptCallback = javascriptCallback;
   }

   public interface JavascriptCallback {
      void onThrstCallback(JavaScriptInterface.ThrstStatusResponse thrstStatusResponse);
   }
}
