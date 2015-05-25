package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;

import javax.inject.Inject;

@Layout(R.layout.fragment_webview)
@MenuResource(R.menu.menu_mock)
public class OtaFragment extends ActualTokenStaticInfoFragment {

    @Inject
    StaticPageProvider provider;

    @Override
    protected String getURL() {
        getPresenter().track(Route.OTA);
        return provider.getoTAPageURL();
    }

}
