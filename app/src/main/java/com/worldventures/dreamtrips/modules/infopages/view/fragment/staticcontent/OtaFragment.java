package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;

import javax.inject.Inject;

@Layout(R.layout.fragment_webview)
public class OtaFragment extends ActualTokenStaticInfoFragment {

    @Inject
    StaticPageProvider provider;

    @Override
    protected String getURL() {
        return provider.getoTAPageURL();
    }

}
