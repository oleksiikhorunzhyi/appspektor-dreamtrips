package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.utils.event.ScreenChangedEvent;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.modules.membership.bundle.UrlBundle;

import javax.inject.Inject;

import butterknife.InjectView;
import timber.log.Timber;

import static com.techery.spares.utils.ui.OrientationUtil.lockOrientation;
import static com.techery.spares.utils.ui.OrientationUtil.unlockOrientation;

@Layout(R.layout.fragment_webview)
public abstract class StaticInfoFragment<T extends WebViewFragmentPresenter> extends BaseFragmentWithArgs<T, UrlBundle>
        implements WebViewFragmentPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    public static final String PRIVACY_TITLE = "Privacy Policy";
    public static final String COOKIE_TITLE = "Cookie Policy";
    public static final String FAQ_TITLE = "FAQ";
    public static final String TERMS_TITLE = "Terms of Use";

    @Inject
    protected StaticPageProvider provider;

    @InjectView(R.id.web_view)
    protected WebView webView;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.progressBarWeb)
    protected ProgressBar progressBarWeb;

    protected Bundle savedState;
    protected boolean isLoading;

    private WeakHandler weakHandler;

    @Override
    protected T createPresenter(Bundle savedInstanceState) {
        return (T) new WebViewFragmentPresenter(getURL());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakHandler = new WeakHandler();
        if (isWebViewSavedState(savedInstanceState)) {
            savedState = savedInstanceState;
        }
    }

    @Override
    public UrlBundle getArgs() {
        return super.getArgs();
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
        webView.setWebViewClient(new WebViewClient() {

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                loadErrorText(view, error.getErrorCode());
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) loadErrorText(view, errorCode);
            }

            private void loadErrorText (WebView webView, int errorCode){
                switch (errorCode) {
                    case ERROR_HOST_LOOKUP:
                        webView.loadData(getString(R.string.error_webview_no_internet), "text", "utf-8");
                        break;
                    default:
                        webView.loadData(getString(R.string.error_webview_default), "text", "utf-8");
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
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

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Timber.d("Page started");
                isLoading = true;
                weakHandler.post(() -> {
                    if (refreshLayout != null) refreshLayout.setRefreshing(true);
                });
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
        });
        if (savedState != null) webView.restoreState(savedState);
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
            if (ViewUtils.isVisibleOnScreen(this)) {
                lockHandler.postDelayed(() -> lockOrientation(getActivity()), 300L);
            } else {
                unlockOrientation(getActivity());
            }
        }, 500L);
    }

    protected void unlockOrientationIfNeeded() {
        if (ViewUtils.isVisibleOnScreen(this)) unlockOrientation(getActivity());
    }

    @Override
    public void onRefresh() {
        getPresenter().onReload();
    }

    @Layout(R.layout.fragment_webview)
    public static class TermsOfServiceFragment extends StaticInfoFragment {
        @Override
        protected String getURL() {
            return provider.getStaticInfoUrl(TERMS_TITLE);
        }

        @Override
        public void afterCreateView(View rootView) {
            super.afterCreateView(rootView);
            ((WebViewFragmentPresenter) getPresenter()).track(Route.TERMS_OF_SERVICE);
        }
    }

    @Layout(R.layout.fragment_webview)
    @MenuResource(R.menu.menu_mock)
    public static class FAQFragment extends StaticInfoFragment {
        @Override
        protected String getURL() {
            return provider.getStaticInfoUrl(FAQ_TITLE);
        }

        @Override
        public void afterCreateView(View rootView) {
            super.afterCreateView(rootView);
            ((WebViewFragmentPresenter) getPresenter()).track(Route.FAQ);
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class PrivacyPolicyFragment extends StaticInfoFragment {
        @Override
        protected String getURL() {
            return provider.getStaticInfoUrl(PRIVACY_TITLE);
        }

        @Override
        public void afterCreateView(View rootView) {
            super.afterCreateView(rootView);
            ((WebViewFragmentPresenter) getPresenter()).track(Route.PRIVACY_POLICY);
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class EnrollFragment extends StaticInfoFragment<WebViewFragmentPresenter> {
        @Override
        protected String getURL() {
            return provider.getEnrollUrl();
        }

        @Override
        public void afterCreateView(View rootView) {
            super.afterCreateView(rootView);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
        }
    }


    @Layout(R.layout.fragment_webview)
    public static class CookiePolicyFragment extends StaticInfoFragment {
        @Override
        protected String getURL() {
            return provider.getStaticInfoUrl(COOKIE_TITLE);
        }

        @Override
        public void afterCreateView(View rootView) {
            super.afterCreateView(rootView);
            ((WebViewFragmentPresenter) getPresenter()).track(Route.COOKIE_POLICY);
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class EnrollRepFragment extends StaticInfoFragment<WebViewFragmentPresenter> {
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
    }

    @Layout(R.layout.fragment_webview)
    public static class BookItFragment extends BundleUrlFragment {
    }

    @Layout(R.layout.fragment_webview)
    public static class BundleUrlFragment extends StaticInfoFragment {

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
