package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;

import com.worldventures.dreamtrips.modules.infopages.presenter.AuthorizedStaticInfoPresenter;

public abstract class AuthorizedStaticInfoFragment extends StaticInfoFragment<AuthorizedStaticInfoPresenter> implements AuthorizedStaticInfoPresenter.View {

    @Override
    public void afterCreateView(View rootView) {
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCachePath("/data/data/com.worldventures.dreamtrips/cache");
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        super.afterCreateView(rootView);
    }

    @Override
    protected AuthorizedStaticInfoPresenter createPresenter(Bundle savedInstanceState) {
        return new AuthorizedStaticInfoPresenter(getURL());
    }

}
