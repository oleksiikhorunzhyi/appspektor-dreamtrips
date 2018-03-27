package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.webview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;


/**
 * This class serves as a WebChromeClient to be set to a WebView, allowing it to play video.
 * Video will play differently depending on target API level (in-line, fullscreen, or both).
 * <p>
 * It has been tested with the following video classes:
 * - android.widget.VideoView (typically API level <11)
 * - android.webkit.HTML5VideoFullScreen$VideoSurfaceView/VideoTextureView (typically API level 11-18)
 * - com.android.org.chromium.content.browser.ContentVideoView$VideoSurfaceView (typically API level 19+)
 * <p>
 * Important notes:
 * - For API level 11+, android:hardwareAccelerated="true" must be set in the application manifest.
 * - The invoking activity must call VideoEnabledWebChromeClient's onBackPressed() inside of its own onBackPressed().
 * - Tested in Android API levels 8-19. Only tested on http://m.youtube.com.
 *
 * @author Cristian Perez (http://cpr.name)
 */
public class DefaultVideoEnabledWebChromeClient extends VideoEnabledWebChromeClient {

   private final String fileUploadPromptLabel;
   private final OpenFileInputAction openFileInputAction;

   public DefaultVideoEnabledWebChromeClient(View activityNonVideoView, ViewGroup activityVideoView,
         View loadingView, VideoEnabledWebView webView, String fileUploadPromptLabel, OpenFileInputAction openFileInputAction) {
      super(activityNonVideoView, activityVideoView, loadingView, webView);
      this.openFileInputAction = openFileInputAction;
      this.fileUploadPromptLabel = fileUploadPromptLabel;
   }

   // file upload callback (Android 2.2 (API level 8) -- Android 2.3 (API level 10)) (hidden method)
   @SuppressWarnings("unused")
   public void openFileChooser(ValueCallback<Uri> uploadMsg) {
      openFileChooser(uploadMsg, null);
   }

   // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
   public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
      openFileChooser(uploadMsg, acceptType, null);
   }

   // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
   @SuppressWarnings("unused")
   public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
      openFileInput(uploadMsg, null);
   }

   // file upload callback (Android 5.0 (API level 21) -- current) (public method)
   @SuppressWarnings("all")
   @Override
   public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
      openFileInput(null, filePathCallback);
      return true;
   }

   @SuppressLint("NewApi")
   protected void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond) {
      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      intent.setType("*/*");
      openFileInputAction.onOpenFileInput(fileUploadCallbackFirst, fileUploadCallbackSecond, Intent.createChooser(intent, fileUploadPromptLabel));
   }

   /**
    * Returns whether file uploads can be used on the current device (generally all platform versions except for 4.4)
    * <p>
    * On Android 4.4.3/4.4.4, file uploads may be possible but will come with a wrong MIME type
    *
    * @param needsCorrectMimeType whether a correct MIME type is required for file uploads or `application/octet-stream` is acceptable
    * @return whether file uploads can be used
    */
   public boolean isFileUploadAvailable(final boolean needsCorrectMimeType) {
      if (Build.VERSION.SDK_INT == 19) {
         final String platformVersion = (Build.VERSION.RELEASE == null) ? "" : Build.VERSION.RELEASE;
         return !needsCorrectMimeType && (platformVersion.startsWith("4.4.3") || platformVersion.startsWith("4.4.4"));
      } else {
         return true;
      }
   }

   public interface OpenFileInputAction {
      void onOpenFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond, Intent intent);
   }
}
