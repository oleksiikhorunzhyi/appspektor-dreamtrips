package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;

@Layout(R.layout.fragment_webview)
public class OtaFragment extends ActualTokenStaticInfoFragment {

    @Override
    protected String getURL() {
        AppConfig config = getPresenter().getConfig();
        AppConfig.URLS urls = config.getUrls();
        AppConfig.URLS.Config configs = BuildConfig.DEBUG ? urls.getProduction() : urls.getQA();
        UserSession userSession = getPresenter().getCurrentUser();
        String url = configs.getoTAPageBaseURL()
                .replace(AppConfig.USER_ID, userSession.getUser().getUsername())
                .replace(AppConfig.TOKEN, userSession.getLegacyApiToken());
        return url;
    }

}
