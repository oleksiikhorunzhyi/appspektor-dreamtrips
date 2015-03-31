package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;

import butterknife.InjectView;

@Layout(R.layout.fragment_webview)
public abstract class StaticInfoFragment<T extends WebViewFragmentPresenter> extends BaseFragment<T> implements WebViewFragmentPresenter.View {

    @InjectView(R.id.web_view)
    protected WebView webView;

    @InjectView(R.id.progressBarWeb)
    protected ProgressBar progressBarWeb;

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
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
        webView.loadUrl(getURL());
    }

    @Override
    public void reload() {
        webView.loadUrl("about:blank");
        webView.loadUrl(getURL());
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
            return "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/terms_of_service.html";
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class FAQFragment extends StaticInfoFragment {

        @Override
        protected String getURL() {
            ((WebViewFragmentPresenter) getPresenter()).track(Route.FAQ);
            return "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/faq.html";
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class PrivacyPolicyFragment extends StaticInfoFragment {

        @Override
        protected String getURL() {
            ((WebViewFragmentPresenter) getPresenter()).track(Route.PRIVACY_POLICY);
            return "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/privacy_policy.html";
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class EnrollFragment extends StaticInfoFragment<WebViewFragmentPresenter> {

        @Override
        protected String getURL() {
            return getPresenter().etEnrollUrl();
        }

        @Override
        public void afterCreateView(View rootView) {
            super.afterCreateView(rootView);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setUseWideViewPort(true);
        }
    }


    @Layout(R.layout.fragment_webview)
    public static class CookiePolicyFragment extends StaticInfoFragment {

        @Override
        protected String getURL() {
            ((WebViewFragmentPresenter) getPresenter()).track(Route.COOKIE_POLICY);
            return "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/cookie_policy.html";
        }
    }


    @Layout(R.layout.fragment_webview)
    public static class TrainingVideosFragment extends StaticInfoFragment {
        @Override
        protected String getURL() {
            AppConfig config = ((WebViewFragmentPresenter) getPresenter()).getConfig();
            AppConfig.URLS urls = config.getUrls();
            AppConfig.URLS.Config configs = BuildConfig.DEBUG ? urls.getProduction() : urls.getQA();
            return configs.getTrainingVideosURL();
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class BookIt extends BundleUrlFragment {
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
            super.afterCreateView(rootView);
            webView.getSettings().setDomStorageEnabled(true);
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
