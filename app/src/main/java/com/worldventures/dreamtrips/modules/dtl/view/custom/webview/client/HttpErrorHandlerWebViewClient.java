package com.worldventures.dreamtrips.modules.dtl.view.custom.webview.client;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class HttpErrorHandlerWebViewClient extends WebViewClient {

    private static final String HTTP = "http";

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return shouldInterceptRequest(view, request.getUrl().toString());
    }

    @SuppressWarnings("deprecation")
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (!url.startsWith(HTTP)) {
            return null;
        }
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            final Response response = okHttpClient
                    .newCall(new Request.Builder()
                            .url(url)
                            .build()
                    ).execute();
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            InputStream inputStream = responseBody.byteStream();
            if (!response.isSuccessful()) {
                onHttpStatusError(url, response.code());
            }
            return new WebResourceResponse("", "", inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected abstract void onHttpStatusError(String url, int statusCode);
}
