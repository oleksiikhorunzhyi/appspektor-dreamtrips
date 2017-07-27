package com.worldventures.dreamtrips.modules.dtl.view.custom.webview.client;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class HttpErrorHandlerWebViewClient extends WebViewClient {

   private static final String HTTP = "http";
   private String token;

   public HttpErrorHandlerWebViewClient(String token) {
      this.token = token;
   }

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
         OkHttpClient okHttpClient = new OkHttpClient.Builder()
               //.newBuilder()
               /*.addInterceptor(new Interceptor() {
                  @Override
                  public Response intercept(Chain chain) throws IOException {
                     final Request original = chain.request();

                     final Request authorized = original.newBuilder()
                           .addHeader("Cookie", getTokenInfo())
                           .build();

                     return chain.proceed(authorized);
                  }
               })*/
               .build();
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

   private String getTokenInfo() {
      return "auth="+token+";domain=.auth="+token;
   }
}
