package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
    public static class FAQFragment extends StaticInfoFragment {

        @Override
        protected String getURL() {
            return "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/terms_of_service.html";
        }
    }

    @Layout(R.layout.fragment_webview)
    public static class TermsAndConditionsFragment extends StaticInfoFragment {

        @Override
        protected String getURL() {
            return "http://gs1.wpc.edgecastcdn.net/80289E/media/1/dtapp/legal/us_en/html/faq.html";
        }
    }
}
