package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;

@Layout(R.layout.fragment_webview)
@MenuResource(R.menu.menu_mock)
public class OtaFragment extends AuthorizedStaticInfoFragment {

    @Override
    protected String getURL() {
        return provider.getOtaPageURL();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        getPresenter().track(Route.OTA);
    }

    @Override
    protected void sendAnalyticEvent(String actionAnalyticEvent) {
        TrackingHelper.actionBookTravelScreen(actionAnalyticEvent);
    }
}
