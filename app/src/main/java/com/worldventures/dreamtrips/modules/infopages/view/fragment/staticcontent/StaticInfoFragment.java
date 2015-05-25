package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.fragment_webview)
public abstract class StaticInfoFragment<T extends WebViewFragmentPresenter> extends BaseFragment<T>
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

    @Override
    protected T createPresenter(Bundle savedInstanceState) {
        return (T) new WebViewFragmentPresenter(getURL());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.endsWith(".pdf")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (refreshLayout != null) {
                    refreshLayout.setRefreshing(true);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (refreshLayout != null) {
                    refreshLayout.setRefreshing(false);

                }
            }
        });
        webView.loadUrl(getPresenter().getLocalizedUrl());
    }

    @Override
    public void reload() {
        webView.loadUrl("about:blank");
        webView.loadUrl(getPresenter().getLocalizedUrl());
    }

    abstract protected String getURL();

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onDestroyView() {
        webView.loadUrl("about:blank");
        webView.destroy();
        webView = null;
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        reload();
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
    public static class TrainingVideosFragment extends StaticInfoFragment {
        @Override
        protected String getURL() {
            return provider.getTrainingVideosURL();
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
        public static final String URL_EXTRA = "URL_EXTRA";
        private String url;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            url = getArguments().getString(URL_EXTRA);
        }

        @Override
        public void afterCreateView(View rootView) {
            webView.getSettings().setDomStorageEnabled(true);
            super.afterCreateView(rootView);
        }

        @Override
        protected String getURL() {
            return url;
        }
    }

}
