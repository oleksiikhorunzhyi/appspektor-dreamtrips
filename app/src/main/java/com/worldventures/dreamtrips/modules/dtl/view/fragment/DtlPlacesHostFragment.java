package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMerchantDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMapBundle;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlacesHostPresenter;

import butterknife.InjectView;

@Layout(R.layout.fragment_dtl_places_host)
public class DtlPlacesHostFragment
        extends BaseFragmentWithArgs<DtlPlacesHostPresenter, PlacesBundle>
        implements DtlPlacesHostPresenter.View {

    @InjectView(R.id.dtl_landscape_slave_container)
    View landscapeSlave;

    @Override
    protected DtlPlacesHostPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlPlacesHostPresenter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showMaster();
        tryShowSlave();
    }

    private void showMaster() {
        Fragment placesTabsFragment = getChildFragmentManager().findFragmentById(R.id.dtl_master_container);
        if (placesTabsFragment != null && placesTabsFragment.getClass().getName()
                .equals(Route.DTL_MERCHANTS_TABS.getClazzName())) return;
        router.moveTo(Route.DTL_MERCHANTS_TABS, NavigationConfigBuilder.forFragment()
                .containerId(R.id.dtl_master_container)
                .backStackEnabled(false)
                .fragmentManager(getChildFragmentManager())
                .data(getArgs())
                .build());
    }

    /**
     * Detect if this is tablet landscape and show slave, if not - remove slave fragment if present
     */
    private void tryShowSlave() {
        if (tabletAnalytic.isTabletLandscape()) {
            router.moveTo(Route.DTL_MAP, NavigationConfigBuilder.forFragment()
                    .containerId(R.id.dtl_landscape_slave_container)
                    .backStackEnabled(false)
                    .fragmentManager(getChildFragmentManager())
                    .data(new DtlMapBundle(getArgs().getLocation(), true))
                    .build());
            landscapeSlave.setVisibility(View.VISIBLE);
        } else {
            removeDetails();
            landscapeSlave.setVisibility(View.GONE);
        }
    }

    private void removeDetails() {
        NavigationConfig navigationConfig = NavigationConfigBuilder.forRemoval()
                .fragmentManager(getChildFragmentManager())
                .containerId(R.id.dtl_landscape_slave_container)
                .build();
        // do both - underlying code will safely determine what to delete
        router.moveTo(Route.DTL_MERCHANT_DETAILS, navigationConfig);
        router.moveTo(Route.DTL_MAP, navigationConfig);
    }

    @Override
    public void showDetails(String merchantId) {
        removeDetails();
        router.moveTo(Route.DTL_MERCHANT_DETAILS, NavigationConfigBuilder.forFragment()
                .containerId(R.id.dtl_landscape_slave_container)
                .backStackEnabled(true)
                .fragmentManager(getChildFragmentManager())
                .data(new DtlMerchantDetailsBundle(merchantId, true))
                .build());
    }
}
