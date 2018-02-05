package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.webview;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.worldventures.core.utils.CrashlyticsTracker;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.StaticInfoFragment;

public class DtWebViewClient extends WebViewClient {

   private final PageStartedAction pageStartedAction;
   private final PageFinishedAction pageFinishedAction;
   private final ReceiveErrorAction receiveErrorAction;
   private final ReceiveErrorAction receiveHttpErrorAction;
   private final ShouldOverrideAction shouldOverrideAction;

   public DtWebViewClient(PageStartedAction pageStartedAction, PageFinishedAction pageFinishedAction,
         ReceiveErrorAction receiveErrorAction, ReceiveErrorAction receiveHttpErrorAction,
         ShouldOverrideAction shouldOverrideAction) {
      this.pageStartedAction = pageStartedAction;
      this.pageFinishedAction = pageFinishedAction;
      this.receiveErrorAction = receiveErrorAction;
      this.receiveHttpErrorAction = receiveHttpErrorAction;
      this.shouldOverrideAction = shouldOverrideAction;
   }

   @Override
   public void onPageStarted(WebView view, String url, Bitmap favicon) {
      super.onPageStarted(view, url, favicon);
      pageStartedAction.onPageLoaded(url);
   }

   @Override
   public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      pageFinishedAction.onPageFinished(url);
   }

   @Override
   public boolean shouldOverrideUrlLoading(WebView view, String url) {
      return shouldOverride(view, url);
   }

   private boolean shouldOverride(WebView view, String url) {
      if (url.startsWith("mailto:")) {
         shouldOverrideAction.onShouldOverride(Intent.createChooser(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)),
               view.getContext().getString(R.string.email_app_choose_dialog_title)));
         view.reload();
         return true;
      }

      if (url.endsWith(".pdf")) {
         shouldOverrideAction.onShouldOverride(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
         return true;
      }

      return false;
   }

   @TargetApi(Build.VERSION_CODES.M)
   @Override
   public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
      super.onReceivedError(view, request, error);
      receiveErrorAction.onReceiveError(error.getErrorCode());
   }

   @Override
   public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
      super.onReceivedError(view, errorCode, description, failingUrl);
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
         receiveErrorAction.onReceiveError(errorCode);
      }
   }

   @Override
   public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
      super.onReceivedHttpError(view, request, errorResponse);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         receiveHttpErrorAction.onReceiveError(errorResponse.getStatusCode());
      }
   }

   @Override
   public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
      handler.cancel();
      CrashlyticsTracker.trackError(new IllegalStateException("Can't load web page due to ssl error:\n" + error));
      receiveErrorAction.onReceiveError(StaticInfoFragment.SECURE_CONNECTION_ERROR);
   }

   public interface PageStartedAction {
      void onPageLoaded(String url);
   }

   public interface PageFinishedAction {
      void onPageFinished(String url);
   }

   public interface ReceiveErrorAction {
      void onReceiveError(int errorCode);
   }

   public interface ShouldOverrideAction {
      void onShouldOverride(Intent intent);
   }

}
