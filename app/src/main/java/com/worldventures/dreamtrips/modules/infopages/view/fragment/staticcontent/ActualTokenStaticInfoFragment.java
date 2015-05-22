package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ProgressBar;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.infopages.presenter.ActualTokenStaticInfoPresenter;

import butterknife.InjectView;

public abstract class ActualTokenStaticInfoFragment extends StaticInfoFragment<ActualTokenStaticInfoPresenter> implements ActualTokenStaticInfoPresenter.View {

    @InjectView(R.id.progressBarWeb)
    protected ProgressBar progressBarWeb;

    @Override
    public void afterCreateView(View rootView) {
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCachePath("/data/data/com.worldventures.dreamtrips/cache");
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        super.afterCreateView(rootView);

        getPresenter().loadUrl();
    }


    @Override
    protected ActualTokenStaticInfoPresenter createPresenter(Bundle savedInstanceState) {
        return new ActualTokenStaticInfoPresenter(getURL());
    }


    @Override
    public void loadContent() {
        webView.loadUrl(getPresenter().getLocalizedUrl());
    }
}
