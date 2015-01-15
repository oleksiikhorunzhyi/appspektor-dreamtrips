package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.presentation.WebViewFragmentPresentation;

import butterknife.ButterKnife;
import butterknife.InjectView;

@Layout(R.layout.fragment_webview)
public class WebViewFragment extends BaseFragment<WebViewFragmentPresentation> {

    public static final String HTTP_URL = "HTTP_URL";

    @InjectView(R.id.web_view)
    WebView webView;

    @Override
    protected WebViewFragmentPresentation createPresentationModel(Bundle savedInstanceState) {
        return new WebViewFragmentPresentation(this);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        String url = getArguments().getString(HTTP_URL);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        webView.loadUrl(url);
    }

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
}
