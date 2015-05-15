package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.storage.complex_objects.ComplexObjectStorage;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.LocaleUtils;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.fragment_webview)
public abstract class StaticInfoFragment<T extends WebViewFragmentPresenter> extends BaseFragment<T>
        implements WebViewFragmentPresenter.View {

    public static final String PRIVACY_TITLE = "Privacy Policy";
    public static final String COOKIE_TITLE = "Cookie Policy";
    public static final String FAQ_TITLE = "FAQ";
    public static final String TERMS_TITLE = "Terms of Use";

    @InjectView(R.id.web_view)
    protected WebView webView;

    @InjectView(R.id.progressBarWeb)
    protected ProgressBar progressBarWeb;

    @Inject
    ComplexObjectStorage<ArrayList<AvailableLocale>> localesStorage;

    @Override
    protected T createPresenter(Bundle savedInstanceState) {
        return (T) new WebViewFragmentPresenter(this);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

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
                progressBarWeb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBarWeb.setVisibility(View.GONE);
            }
        });
        webView.loadUrl(LocaleUtils.substituteActualLocale(getActivity(), getURL(), localesStorage));
    }

    @Override
    public void reload() {
        webView.loadUrl("about:blank");
        webView.loadUrl(LocaleUtils.substituteActualLocale(getActivity(), getURL(), localesStorage));
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

    @Layout(R.layout.fragment_webview)
    public static class TermsOfServiceFragment extends StaticInfoFragment {

        @Override
        protected String getURL() {
            ((WebViewFragmentPresenter) getPresenter()).track(Route.TERMS_OF_SERVICE);
            return ((WebViewFragmentPresenter) getPresenter()).getStaticInfoUrl(TERMS_TITLE);
        }
    }

    @Layout(R.layout.fragment_webview)
    @MenuResource(R.menu.menu_mock)
    public static class FAQFragment extends StaticInfoFragment {

        @Override
        protected String getURL() {
            ((WebViewFragmentPresenter) getPresenter()).track(Route.FAQ);
            return ((WebViewFragmentPresenter) getPresenter()).getStaticInfoUrl(FAQ_TITLE);
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class PrivacyPolicyFragment extends StaticInfoFragment {

        @Override
        protected String getURL() {
            ((WebViewFragmentPresenter) getPresenter()).track(Route.PRIVACY_POLICY);
            return ((WebViewFragmentPresenter) getPresenter()).getStaticInfoUrl(PRIVACY_TITLE);
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class EnrollFragment extends StaticInfoFragment<WebViewFragmentPresenter> {

        @Override
        protected String getURL() {
            return getPresenter().getEnrollUrl();
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
            ((WebViewFragmentPresenter) getPresenter()).track(Route.COOKIE_POLICY);
            return ((WebViewFragmentPresenter) getPresenter()).getStaticInfoUrl(COOKIE_TITLE);
        }
    }


    @Layout(R.layout.fragment_webview)
    public static class TrainingVideosFragment extends StaticInfoFragment {
        @Override
        protected String getURL() {
            AppConfig.URLS.Config config = ((WebViewFragmentPresenter) getPresenter()).getConfig();
            return config.getTrainingVideosURL();
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class EnrollRepFragment extends StaticInfoFragment<WebViewFragmentPresenter> {

        @Override
        protected String getURL() {
            return getPresenter().getEnrollRepUrl();
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
        public void onDestroyView() {
            super.onDestroy();
        }

        @Override
        protected String getURL() {
            return url;
        }
    }

}
