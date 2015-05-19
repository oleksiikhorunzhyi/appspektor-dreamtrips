package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ProgressBar;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.infopages.presenter.ActualTokenStaticInfoFragmentPM;

import butterknife.InjectView;

public abstract class ActualTokenStaticInfoFragment extends StaticInfoFragment<ActualTokenStaticInfoFragmentPM> implements ActualTokenStaticInfoFragmentPM.View {

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
    protected ActualTokenStaticInfoFragmentPM createPresenter(Bundle savedInstanceState) {
        return new ActualTokenStaticInfoFragmentPM(getURL());
    }


    @Override
    public void loadContent() {
        webView.loadUrl(getPresenter().getLocalizedUrl());
    }
}
