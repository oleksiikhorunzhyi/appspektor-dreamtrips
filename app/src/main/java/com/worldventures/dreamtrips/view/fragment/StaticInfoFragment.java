package com.worldventures.dreamtrips.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.presentation.WebViewFragmentPresentation;

import butterknife.InjectView;

@Layout(R.layout.fragment_webview)
public abstract class StaticInfoFragment extends BaseFragment<WebViewFragmentPresentation> {

    @InjectView(R.id.web_view)
    WebView webView;

    @Override
    protected WebViewFragmentPresentation createPresentationModel(Bundle savedInstanceState) {
        return new WebViewFragmentPresentation(this);
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
            return "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/terms_of_service.html";
        }
    }
    @Layout(R.layout.fragment_webview)
    public static class FAQFragment extends StaticInfoFragment {

        @Override
        protected String getURL() {
            return "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/faq.html";
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class PrivacyPolicyFragment extends StaticInfoFragment {

        @Override
        protected String getURL() {
            return "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/privacy_policy.html";
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class EnrollFragment extends StaticInfoFragment {

        @Override
        protected String getURL() {
            return "https://secure.worldventures.biz/(S(ypszgovffsbbiekgosdwysop))/Checkout/PreRequisite.aspx?did={BASE64_ENCODED_USERID}&pn=UkVUQUlM&sa=ZHQ=";
        }
    }


    @Layout(R.layout.fragment_webview)
    public static class CookiePolicyFragment extends StaticInfoFragment {

        @Override
        protected String getURL() {
            return "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/cookie_policy.html";
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class BookItFragment extends StaticInfoFragment {
        public static final String URL_EXTRA = "URL_EXTRA";
        private String url;

        @InjectView(R.id.progressBarWeb)
        ProgressBar progressBarWeb;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            url = getArguments().getString(URL_EXTRA);
        }

        @Override
        public void afterCreateView(View rootView) {
            super.afterCreateView(rootView);
            webView.getSettings().setDomStorageEnabled(true);
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
