package com.worldventures.dreamtrips.modules.dtl.view.custom.webview.javascript;

import android.webkit.JavascriptInterface;

public abstract class JavaScriptInterface {
   public static final String JAVASCRIPT_INTERFACE = "mobileTHRSTContext";

   @JavascriptInterface
   @SuppressWarnings("unused")
   public void thrstTransactionCompleted(String message) {
      thrstCallback(message);
   }

   public abstract void thrstCallback(String message);

   @Override
   public String toString() {
      return JAVASCRIPT_INTERFACE;
   }
}
