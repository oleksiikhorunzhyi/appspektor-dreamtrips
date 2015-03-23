package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.infopages.presenter.ActualTokenStaticInfoFragmentPM;

import butterknife.InjectView;

public abstract class ActualTokenStaticInfoFragment extends StaticInfoFragment<ActualTokenStaticInfoFragmentPM> implements ActualTokenStaticInfoFragmentPM.View {

    @InjectView(R.id.progressBarWeb)
    ProgressBar progressBarWeb;

    @Override
    public void afterCreateView(View rootView) {
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCachePath("/data/data/com.worldventures.dreamtrips/cache");
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setJavaScriptEnabled(true);

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

        getPresenter().loadUrl();
    }


    @Override
    protected ActualTokenStaticInfoFragmentPM createPresenter(Bundle savedInstanceState) {
        return new ActualTokenStaticInfoFragmentPM(this);
    }


    @Override
    public void loadContent() {
        webView.loadUrl(getURL());
    }
}
