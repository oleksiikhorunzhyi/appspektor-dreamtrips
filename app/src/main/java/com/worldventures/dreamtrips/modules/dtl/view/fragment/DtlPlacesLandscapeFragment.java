package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlaceDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlacesLandscapePresenter;

@Layout(R.layout.fragment_dtl_places_landscape)
public class DtlPlacesLandscapeFragment
        extends BaseFragmentWithArgs<DtlPlacesLandscapePresenter, PlacesBundle>
        implements DtlPlacesLandscapePresenter.View {

    @Override
    protected DtlPlacesLandscapePresenter createPresenter(Bundle savedInstanceState) {
        return new DtlPlacesLandscapePresenter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showMaster();
        showSlave();
    }

    private void showMaster() {
        moveToRoute(Route.DTL_PLACES_LIST, R.id.dtl_landscape_master_container);
    }

    private void showSlave() {
        moveToRoute(Route.DTL_MAP, R.id.dtl_landscape_slave_container);
    }

    private void moveToRoute(Route route, @IdRes int containerId) {
        router.moveTo(route, NavigationConfigBuilder.forFragment()
                .containerId(containerId)
                .backStackEnabled(false)
                .fragmentManager(getChildFragmentManager())
                .data(getArgs())
                .build());
    }

    @Override
    public void showDetails(DtlPlace place) {
        router.moveTo(Route.DTL_PLACE_DETAILS, NavigationConfigBuilder.forFragment()
                .containerId(R.id.dtl_landscape_slave_container)
                .backStackEnabled(false)
                .fragmentManager(getChildFragmentManager())
                .data(new PlaceDetailsBundle(place, true))
                .build());
    }
}
