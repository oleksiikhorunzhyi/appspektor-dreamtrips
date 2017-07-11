package com.worldventures.dreamtrips.modules.dtl.view.custom.webview.javascript;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class JavaScriptInterface {
    public static final String JAVASCRIPT_INTERFACE = "mobileTHRSTContext";

    public JavaScriptInterface() {
    }

    @JavascriptInterface
    public void sendMessage(String message) {
        Log.d("From javascript", message);
    }

    @Override
    public String toString() {
        return JAVASCRIPT_INTERFACE;
    }
}
