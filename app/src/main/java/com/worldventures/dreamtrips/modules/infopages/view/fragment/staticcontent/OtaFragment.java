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
        UserSession userSession = getPresenter().getCurrentUser();
        return getPresenter().getConfig().getoTAPageURL()
                .replace(AppConfig.USER_ID, userSession.getUser().getUsername())
                .replace(AppConfig.TOKEN, userSession.getLegacyApiToken());
    }

}
