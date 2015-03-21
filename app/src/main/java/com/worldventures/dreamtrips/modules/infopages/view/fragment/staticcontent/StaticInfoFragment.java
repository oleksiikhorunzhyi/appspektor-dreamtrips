package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.S3GlobalConfig;
import com.worldventures.dreamtrips.modules.infopages.model.URLS;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import butterknife.InjectView;

@Layout(R.layout.fragment_webview)
public abstract class StaticInfoFragment<T extends WebViewFragmentPresenter> extends BaseFragment<T> {

    @InjectView(R.id.web_view)
    protected WebView webView;

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
        });
        webView.loadUrl(getURL());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    abstract protected String getURL();

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
        webView.resumeTimers();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
        webView.pauseTimers();
    }

    @Override
    public void onDestroyView() {
        webView.destroy();
        super.onDestroyView();
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
        @InjectView(R.id.progressBarWeb)
        ProgressBar progressBarWeb;

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
            webView.setWebViewClient(new WebViewClient() {

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
            S3GlobalConfig config = ((WebViewFragmentPresenter) getPresenter()).getConfig();
            URLS urls = config.getUrls();
            URLS.Config configs = BuildConfig.DEBUG ? urls.getProduction() : urls.getQA();
            return configs.getTrainingVideosURL();
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class BookIt extends BundleUrlFragment {
    }

    @Layout(R.layout.fragment_webview)
    public static class BundleUrlFragment extends StaticInfoFragment {
        public static final String URL_EXTRA = "URL_EXTRA";
        @InjectView(R.id.progressBarWeb)
        ProgressBar progressBarWeb;
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
            webView.getSettings().setAppCachePath("/data/data/com.worldventures.dreamtrips/cache");
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            webView.setWebViewClient(new WebViewClient() {

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
