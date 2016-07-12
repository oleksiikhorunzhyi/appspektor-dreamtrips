package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.badoo.mobile.util.WeakHandler;
import com.messenger.util.CrashlyticsTracker;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.utils.event.ScreenChangedEvent;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.dialog.MessageDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.modules.membership.bundle.UrlBundle;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.InjectView;
import timber.log.Timber;

import static com.techery.spares.utils.ui.OrientationUtil.lockOrientation;
import static com.techery.spares.utils.ui.OrientationUtil.unlockOrientation;

@Layout(R.layout.fragment_webview)
public abstract class StaticInfoFragment<T extends WebViewFragmentPresenter, P extends Parcelable>
        extends BaseFragmentWithArgs<T, P>
        implements WebViewFragmentPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    protected StaticPageProvider provider;

    @InjectView(R.id.web_view)
    protected VideoEnabledWebView webView;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.nonVideoLayout)
    View nonVideoLayout;
    @InjectView(R.id.videoLayout)
    ViewGroup videoLayout;

    protected Bundle savedState;
    protected boolean isLoading;

    private WeakHandler weakHandler;

    protected static final int REQUEST_CODE_FILE_PICKER = 51426;
    protected int mRequestCodeFilePicker = REQUEST_CODE_FILE_PICKER;
    protected WeakReference<Fragment> fragment;
    protected WeakReference<Activity> activity;

    private MessageDialogFragment errorFragment;
    static final int SECURE_CONNECTION_ERROR = 21;

    /**
     * File upload callback for platform versions prior to Android 5.0
     */
    protected ValueCallback<Uri> mFileUploadCallbackFirst;
    /**
     * File upload callback for Android 5.0+
     */
    protected ValueCallback<Uri[]> mFileUploadCallbackSecond;

    @Override
    protected T createPresenter(Bundle savedInstanceState) {
        return (T) new WebViewFragmentPresenter(getURL());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment = new WeakReference<>(this);
        weakHandler = new WeakHandler();
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
        return savedInstanceState != null &&
                (savedInstanceState.containsKey("WEBVIEW_CHROMIUM_STATE")
                        || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                sendAnalyticEvent(TrackingHelper.ATTRIBUTE_VIEW);
                Timber.d("Page started");
                isLoading = true;
                weakHandler.post(() -> {
                    if (refreshLayout != null) refreshLayout.setRefreshing(true);
                });
                cleanError();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Timber.d("Page finished");
                isLoading = false;
                if (!(isDetached() || isRemoving() || refreshLayout == null)) {
                    weakHandler.post(() -> {
                        if (refreshLayout != null) refreshLayout.setRefreshing(false);
                    });
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mailto:")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.email_app_choose_dialog_title)));
                    view.reload();
                    return true;
                }

                if (url.endsWith(".pdf")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                return false;
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                showError(error.getErrorCode());
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) showError(errorCode);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.cancel();
                CrashlyticsTracker.trackError(new IllegalStateException("Can't load web page due to ssl error:\n" + error));
                showError(SECURE_CONNECTION_ERROR);
            }

        });

        VideoEnabledWebChromeClient webChromeClient =
                new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, null, webView) {
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
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                openFileInput(null, filePathCallback);
                return true;
            }

            @SuppressLint("NewApi")
            protected void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst,
                                         final ValueCallback<Uri[]> fileUploadCallbackSecond) {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                }
                mFileUploadCallbackFirst = fileUploadCallbackFirst;

                if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                }
                mFileUploadCallbackSecond = fileUploadCallbackSecond;

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");

                if (fragment != null && fragment.get() != null && Build.VERSION.SDK_INT >= 11) {
                    fragment.get().startActivityForResult(Intent.createChooser(i, getFileUploadPromptLabel()), mRequestCodeFilePicker);
                } else if (activity != null && activity.get() != null) {
                    activity.get().startActivityForResult(Intent.createChooser(i, getFileUploadPromptLabel()), mRequestCodeFilePicker);
                }
            }

            /**
             * Returns whether file uploads can be used on the current device (generally all platform versions except for 4.4)
             *
             * @return whether file uploads can be used
             */
            public boolean isFileUploadAvailable() {
                return isFileUploadAvailable(false);
            }

            /**
             * Returns whether file uploads can be used on the current device (generally all platform versions except for 4.4)
             *
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

            protected String getFileUploadPromptLabel() {
                return getString(R.string.choose_gallery);
            }

        };
        webChromeClient.setOnToggledFullscreen(fullscreen -> {
            // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
            AppCompatActivity compatActivity = (AppCompatActivity) StaticInfoFragment.this.activity.get();
            if (fullscreen) {
                if (compatActivity != null && compatActivity.getSupportActionBar() != null) {
                    compatActivity.getSupportActionBar().hide();
                }
                WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                getActivity().getWindow().setAttributes(attrs);
                if (Build.VERSION.SDK_INT >= 14) {
                    getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                }
            } else {
                if (compatActivity != null && compatActivity.getSupportActionBar() != null) {
                    compatActivity.getSupportActionBar().show();
                }
                WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
                attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                getActivity().getWindow().setAttributes(attrs);
                if (Build.VERSION.SDK_INT >= 14) {
                    getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }

        });
        webView.setWebChromeClient(webChromeClient);

        webView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
            }
            return false;
        });

        if (savedState != null) webView.restoreState(savedState);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == mRequestCodeFilePicker) {
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                        mFileUploadCallbackFirst = null;
                    } else if (mFileUploadCallbackSecond != null) {
                        Uri[] dataUris;
                        try {
                            dataUris = new Uri[]{Uri.parse(intent.getDataString())};
                        } catch (Exception e) {
                            dataUris = null;
                        }

                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                }
            } else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                } else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        }
    }

    protected void sendAnalyticEvent(String actionAnalyticEvent) {
    }

    @Override
    public void load(String url) {
        if (!isLoading && savedState == null) webView.loadUrl(url);
    }

    @Override
    public void reload(String url) {
        webView.loadUrl("about:blank");
        webView.loadUrl(url);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        weakHandler.post(() -> {
            if (refreshLayout == null
                    || (refreshLayout.isRefreshing() && refreshing)
                    || (!refreshLayout.isRefreshing() && !refreshing)) return;
            //
            refreshLayout.setRefreshing(refreshing);
        });
    }

    @Override
    public void showError(int errorCode) {
        if (getPresenter() != null) getPresenter().setInErrorState(true);
        if (isDetached() || isRemoving()) return;
        //
        int errorText;
        switch (errorCode) {
            case WebViewClient.ERROR_HOST_LOOKUP:
            case WebViewClient.ERROR_AUTHENTICATION:
                errorText = R.string.error_webview_no_internet;
                break;
            case SECURE_CONNECTION_ERROR:
                errorText = R.string.error_webview_secure_connection;
                break;
            default:
                errorText = R.string.error_webview_default;
                break;
        }
        errorFragment = MessageDialogFragment.create(errorText);
        getChildFragmentManager()
                .beginTransaction().replace(R.id.web_view, errorFragment)
                .commitAllowingStateLoss();
    }

    private void cleanError() {
        if (getPresenter() != null) {
            getPresenter().setInErrorState(false);
        }
        if (errorFragment != null) {
            getChildFragmentManager().beginTransaction().remove(errorFragment).commitAllowingStateLoss();
            errorFragment = null;
        }
    }

    abstract protected String getURL();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (webView != null) webView.saveState(outState);
    }

    @Override
    public void onResume() {
        lockOrientationIfNeeded();
        webView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onDestroyView() {
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

    ///////////////////////////////////////////////////////////////////////////
    // Orientation locking/unlocking
    ///////////////////////////////////////////////////////////////////////////

    WeakHandler lockHandler = new WeakHandler();

    public void onEventMainThread(ScreenChangedEvent event) {
        lockHandler.removeCallbacksAndMessages(null);
        lockOrientationIfNeeded();
    }

    protected void lockOrientationIfNeeded() {
        lockHandler.postDelayed(() -> {
            if (ViewUtils.isFullVisibleOnScreen(this)) {
                lockHandler.postDelayed(() -> lockOrientation(getActivity()), 300L);
            } else {
                unlockOrientation(getActivity());
            }
        }, 500L);
    }

    protected void unlockOrientationIfNeeded() {
        if (ViewUtils.isFullVisibleOnScreen(this)) unlockOrientation(getActivity());
    }

    @Override
    public void onRefresh() {
        getPresenter().onReload();
    }

    @Layout(R.layout.fragment_webview)
    public static class TermsOfServiceFragment extends StaticInfoFragment {
        @Override
        protected String getURL() {
            return provider.getTermsOfServiceUrl();
        }

        @Override
        public void afterCreateView(View rootView) {
            super.afterCreateView(rootView);
            ((WebViewFragmentPresenter) getPresenter()).track(Route.TERMS_OF_SERVICE);
        }

        @Override
        protected void sendAnalyticEvent(String actionAnalyticEvent) {
            TrackingHelper.actionTermsTab(TrackingHelper.ACTION_TERMS_SERVICE, actionAnalyticEvent);
        }

    }

    @Layout(R.layout.fragment_webview)
    public static class CookiePolicyFragment extends StaticInfoFragment {
        @Override
        protected String getURL() {
            return provider.getCookiePolicyUrl();
        }

        @Override
        public void afterCreateView(View rootView) {
            super.afterCreateView(rootView);
            ((WebViewFragmentPresenter) getPresenter()).track(Route.COOKIE_POLICY);
        }

        @Override
        protected void sendAnalyticEvent(String actionAnalyticEvent) {
            TrackingHelper.actionTermsTab(TrackingHelper.ACTION_TERMS_COOKIE, actionAnalyticEvent);
        }
    }

    @Layout(R.layout.fragment_webview)
    @MenuResource(R.menu.menu_mock)
    public static class FAQFragment extends StaticInfoFragment {

        @Override
        protected String getURL() {
            return provider.getFaqUrl();
        }

        @Override
        public void afterCreateView(View rootView) {
            super.afterCreateView(rootView);
            ((WebViewFragmentPresenter) getPresenter()).track(Route.FAQ);
        }

        @Override
        protected void sendAnalyticEvent(String actionAnalyticEvent) {
            //
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class PrivacyPolicyFragment extends StaticInfoFragment {
        @Override
        protected String getURL() {
            return provider.getPrivacyPolicyUrl();
        }

        @Override
        public void afterCreateView(View rootView) {
            super.afterCreateView(rootView);
            ((WebViewFragmentPresenter) getPresenter()).track(Route.PRIVACY_POLICY);
        }

        @Override
        protected void sendAnalyticEvent(String actionAnalyticEvent) {
            TrackingHelper.actionTermsTab(TrackingHelper.ACTION_TERMS_PRIVACY, actionAnalyticEvent);
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class EnrollMemberFragment extends AuthorizedStaticInfoFragment<UrlBundle> {
        @Override
        protected String getURL() {
            return provider.getEnrollMemberUrl();
        }

        @Override
        public void afterCreateView(View rootView) {
            super.afterCreateView(rootView);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser) getPresenter().track(Route.ENROLL_MEMBER);
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class EnrollMerchantFragment
            extends AuthorizedStaticInfoFragment<MerchantIdBundle> {

        @Override
        protected String getURL() {
            return provider.getEnrollMerchantUrl(getArgs());
        }

        @Override
        public void afterCreateView(View rootView) {
            super.afterCreateView(rootView);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser) getPresenter().track(Route.ENROLL_MERCHANT);
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class EnrollRepFragment extends AuthorizedStaticInfoFragment {

        @Override
        protected String getURL() {
            return provider.getEnrollRepUrl();
        }

        @Override
        public void afterCreateView(View rootView) {
            super.afterCreateView(rootView);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
        }

        @Override
        protected void sendAnalyticEvent(String actionAnalyticEvent) {
            TrackingHelper.actionRepToolsEnrollment(actionAnalyticEvent);
        }

    }

    @Layout(R.layout.fragment_webview)
    public static class BookItFragment extends BundleUrlFragment {

        private static final String BOOK_IT_HEADER_KEY = "DT-Device-Identifier";
        private static final String BOOK_IT_HEADER = "Android" + "-" + Build.VERSION.RELEASE + "-"
                + BuildConfig.versionMajor + "." + BuildConfig.versionMinor + "."
                + BuildConfig.versionPatch;

        @Override
        public void load(String url) {
            if (!isLoading && savedState == null) {
                Map<String, String> additionalHeaders = new HashMap<>();
                additionalHeaders.put(BOOK_IT_HEADER_KEY, BOOK_IT_HEADER);
                webView.loadUrl(url, additionalHeaders);
            }
        }

        @Override
        public void reload(String url) {
            webView.loadUrl("about:blank");
            load(url);
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class BundleUrlFragment<T extends WebViewFragmentPresenter>
            extends StaticInfoFragment<T, UrlBundle> {

        @Override
        protected String getURL() {
            return getArgs().getUrl();
        }

        @Override
        public void afterCreateView(View rootView) {
            webView.getSettings().setDomStorageEnabled(true);
            super.afterCreateView(rootView);
        }
    }
}
