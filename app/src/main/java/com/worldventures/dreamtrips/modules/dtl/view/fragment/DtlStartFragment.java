package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlStartPresenter;

/**
 * Transitional fragment that determines further navigation.<br />
 * Depending on DtlLocation being previously selected it opens Dtl Places List screen (if location was selected) <br />
 * or Dtl Locations screen (if not).
 */
@Layout(R.layout.fragment_dtl_start_empty)
@MenuResource(R.menu.menu_mock)
public class DtlStartFragment extends BaseFragment<DtlStartPresenter> implements DtlStartPresenter.View {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentCompass.disableBackStack();
        fragmentCompass.setSupportFragmentManager(getChildFragmentManager());
        fragmentCompass.setContainerId(R.id.dtl_container);
    }

    @Override
    protected DtlStartPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlStartPresenter();
    }

    @Override
    public void openDtlLocationsScreen() {
        NavigationBuilder
                .create()
                .with(fragmentCompass)
                .move(Route.DTL_LOCATIONS);
    }

    @Override
    public void openDtlPlacesScreen(PlacesBundle bundle) {
        NavigationBuilder
                .create()
                .with(fragmentCompass)
                .data(bundle)
                .move(Route.DTL_PLACES_LIST);
    }
}
