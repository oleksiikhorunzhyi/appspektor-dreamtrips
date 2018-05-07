package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebViewClient;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.util.Utils;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.dialog.MessageDialogFragment;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.extension.ActivityKt;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.webview.DefaultVideoEnabledWebChromeClient;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.webview.DtWebViewClient;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.webview.VideoEnabledWebChromeClient;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.webview.VideoEnabledWebView;
import com.worldventures.dreamtrips.social.util.event_delegate.ScreenChangedEventDelegate;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Map;

import javax.inject.Inject;

import butterknife.InjectView;

import static com.techery.spares.utils.ui.OrientationUtil.lockOrientation;
import static com.techery.spares.utils.ui.OrientationUtil.unlockOrientation;

@Layout(R.layout.fragment_webview)
public abstract class StaticInfoFragment<T extends WebViewFragmentPresenter, P extends Parcelable> extends RxBaseFragmentWithArgs<T, P>
      implements WebViewFragmentPresenter.View, SwipeRefreshLayout.OnRefreshListener {

   @InjectView(R.id.web_view) protected VideoEnabledWebView webView;
   @InjectView(R.id.swipe_container) protected SwipeRefreshLayout refreshLayout;
   @InjectView(R.id.nonVideoLayout) View nonVideoLayout;
   @InjectView(R.id.videoLayout) ViewGroup videoLayout;
   @Inject ScreenChangedEventDelegate screenChangedEventDelegate;

   protected Bundle savedState;
   protected boolean isLoading;
   protected WeakHandler weakHandler = new WeakHandler();
   protected WeakHandler lockHandler = new WeakHandler();
   protected WeakReference<Fragment> fragment;
   protected WeakReference<Activity> activity;
   private MessageDialogFragment errorFragment;

   /**
    * File upload callback for platform versions prior to Android 5.0
    */
   protected ValueCallback<Uri> preLollipopFileUploadCall;
   /**
    * File upload callback for Android 5.0+
    */
   protected ValueCallback<Uri[]> fileUploadCallback;

   public static final int SECURE_CONNECTION_ERROR = 21;
   protected static final int REQUEST_CODE_FILE_PICKER = 51426;
   public static final String BLANK_PAGE = "about:blank";
   private static final String JS_STOP_PLAYBACK = "javascript:var x = document.getElementsByTagName(\"audio\");"
         + " var i; for (i = 0; i < x.length; i++) {x[i].pause();}";

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      fragment = new WeakReference<>(this);
      if (isWebViewSavedState(savedInstanceState)) {
         savedState = savedInstanceState;
      }
   }

   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);
      this.activity = new WeakReference<>(activity);
   }

   private boolean isWebViewSavedState(Bundle savedInstanceState) {
      return savedInstanceState != null && (savedInstanceState.containsKey("WEBVIEW_CHROMIUM_STATE") || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT);
   }

   @SuppressWarnings("unchecked")
   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      this.refreshLayout.setOnRefreshListener(this);
      this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
      settingUpWebView();

      screenChangedEventDelegate.getObservable()
            .compose(bindUntilDropViewComposer())
            .subscribe(event -> {
               lockHandler.removeCallbacksAndMessages(null);
               lockOrientationIfNeeded();
            });

      if (savedState != null) {
         webView.restoreState(savedState);
      }
   }

   @SuppressLint("SetJavaScriptEnabled")
   private void settingUpWebView() {
      webView.getSettings().setJavaScriptEnabled(true);
      webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
      webView.getSettings().setDefaultTextEncodingName("utf-8");
      webView.setWebViewClient(new DtWebViewClient(this::startLoadingPage, getPresenter()::pageLoaded,
            this::showError, this::onReceivedHttpError, this::startActivity));

      VideoEnabledWebChromeClient webChromeClient = new DefaultVideoEnabledWebChromeClient(nonVideoLayout,
            videoLayout, null, webView, getString(R.string.choose_gallery), this::openFileInputAction);

      webChromeClient.setOnToggledFullscreen(fullscreen -> ActivityKt.handleFullScreen(getActivity(), fullscreen));
      webView.setWebChromeClient(webChromeClient);
      webView.setOnKeyListener((v, keyCode, event) -> {
         if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
         }
         return false;
      });
   }

   private void openFileInputAction(ValueCallback<Uri> fileUploadCallbackFirst,
         ValueCallback<Uri[]> fileUploadCallbackSecond, Intent intent) {
      if (preLollipopFileUploadCall != null) {
         preLollipopFileUploadCall.onReceiveValue(null);
      }

      if (this.fileUploadCallback != null) {
         this.fileUploadCallback.onReceiveValue(null);
      }

      preLollipopFileUploadCall = fileUploadCallbackFirst;
      this.fileUploadCallback = fileUploadCallbackSecond;
      startActivityForResult(intent, REQUEST_CODE_FILE_PICKER);
   }

   @Override
   public void hideLoadingProgress() {
      if (!(isDetached() || isRemoving() || refreshLayout == null)) {
         weakHandler.post(() -> {
            if (refreshLayout != null) {
               isLoading = false;
               refreshLayout.setRefreshing(false);
            }
         });
      }
   }

   @Override
   public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
      if (requestCode != REQUEST_CODE_FILE_PICKER) {
         return;
      }

      if (resultCode == Activity.RESULT_OK) {
         if (intent == null) {
            return;
         }

         if (preLollipopFileUploadCall != null) {
            preLollipopFileUploadCall.onReceiveValue(intent.getData());
            preLollipopFileUploadCall = null;
            return;
         }

         if (fileUploadCallback != null) {
            Uri[] dataUris;
            try {
               dataUris = new Uri[]{Uri.parse(intent.getDataString())};
            } catch (Exception e) {
               dataUris = null;
            }
            fileUploadCallback.onReceiveValue(dataUris);
            fileUploadCallback = null;
            return;
         }
      }

      if (preLollipopFileUploadCall != null) {
         preLollipopFileUploadCall.onReceiveValue(null);
         preLollipopFileUploadCall = null;
         return;
      }

      if (fileUploadCallback != null) {
         fileUploadCallback.onReceiveValue(null);
         fileUploadCallback = null;
      }
   }

   protected void onReceivedHttpError(int errorCode) { }

   @Override
   public void load(@NotNull String url, Map<String, String> headers) {
      if (!isLoading && savedState == null) {
         webView.loadUrl(url, headers);
      }
   }

   @Override
   public void reload(@NonNull String url, Map<String, String> headers) {
      webView.loadUrl("about:blank");
      webView.loadUrl(url, headers);
   }

   @Override
   public void setRefreshing(boolean refreshing) {
      weakHandler.post(() -> {
         if (refreshLayout == null) {
            return;
         }
         isLoading = refreshing;
         refreshLayout.setRefreshing(refreshing);
      });
   }

   @Override
   public void showError(int errorCode) {

      if (getPresenter() != null) {
         getPresenter().setInErrorState(true);
      }

      if (cantDisplayError()) {
         return;
      }

      int errorText;
      switch (errorCode) {
         case WebViewClient.ERROR_HOST_LOOKUP:
         case WebViewClient.ERROR_AUTHENTICATION:
            errorText = R.string.error_webview_no_internet;
            noInternetConnection();
            break;
         case SECURE_CONNECTION_ERROR:
            errorText = R.string.error_webview_secure_connection;
            break;
         default:
            errorText = R.string.error_webview_default;
            noInternetConnection();
            break;
      }
      errorFragment = MessageDialogFragment.create(errorText);
      getChildFragmentManager().beginTransaction().replace(R.id.web_view, errorFragment).commitAllowingStateLoss();
   }

   private boolean cantDisplayError() {
      return getActivity() == null || getActivity().isDestroyed() || isDetached() || isRemoving();
   }

   private void noInternetConnection() {
      if (!Utils.isConnected(getContext())) {
         getPresenter().noInternetConnection();
      }
   }

   protected void startLoadingPage(String url) {
      setRefreshing(true);
      if (getPresenter() != null) {
         getPresenter().setInErrorState(false);
      }

      if (errorFragment != null && getActivity() != null) {
         getChildFragmentManager().beginTransaction().remove(errorFragment).commitAllowingStateLoss();
         errorFragment = null;
      }
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      if (webView != null) {
         webView.saveState(outState);
      }
   }

   @Override
   public void onResume() {
      lockOrientationIfNeeded();
      webView.onResume();
      super.onResume();
   }

   @Override
   public void onPause() {
      webView.onPause();
      super.onPause();
   }

   @Override
   public void onDestroyView() {
      webView.loadUrl(JS_STOP_PLAYBACK);
      lockHandler.removeCallbacksAndMessages(null);
      unlockOrientationIfNeeded();
      super.onDestroyView();
   }

   @Override
   public void onDestroy() {
      if (webView != null) {
         webView.destroy();
      }
      super.onDestroy();
   }

   protected void lockOrientationIfNeeded() {
      lockHandler.postDelayed(() -> {
         if (ViewUtils.isFullVisibleOnScreen(this) && getActivity() != null) {
            lockHandler.postDelayed(() -> lockOrientation(getActivity()), 300L);
         } else {
            unlockOrientation(getActivity());
         }
      }, 500L);
   }

   protected void unlockOrientationIfNeeded() {
      if (ViewUtils.isFullVisibleOnScreen(this)) {
         unlockOrientation(getActivity());
      }
   }

   @Override
   public void onRefresh() {
      getPresenter().onReload();
   }
}
