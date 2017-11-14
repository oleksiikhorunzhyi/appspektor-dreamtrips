package com.worldventures.dreamtrips.modules.dtl.view.custom.webview.javascript;

import android.webkit.JavascriptInterface;

import com.google.gson.Gson;

public abstract class JavaScriptInterface {
   public static final String JAVASCRIPT_INTERFACE = "mobileTHRSTContext";

   @JavascriptInterface
   @SuppressWarnings("unused")
   public void thrstTransactionCompleted(String message) {
      thrstCallback(new Gson().fromJson(message, ThrstStatusResponse.class));
   }

   public abstract void thrstCallback(ThrstStatusResponse thrstStatusResponse);

   @Override
   public String toString() {
      return JAVASCRIPT_INTERFACE;
   }

   public class ThrstStatusResponse {
      public String body;
      public String status;
   }
}
