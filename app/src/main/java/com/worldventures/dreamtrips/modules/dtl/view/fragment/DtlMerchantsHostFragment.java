package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMapBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMerchantDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMerchantsHostPresenter;

import butterknife.InjectView;
import icepick.State;

@Layout(R.layout.fragment_dtl_merchants_host)
public class DtlMerchantsHostFragment extends RxBaseFragment<DtlMerchantsHostPresenter>
        implements DtlMerchantsHostPresenter.View {

    @InjectView(R.id.dtl_landscape_slave_container)
    View landscapeSlave;
    //
    /**
     * Severe monkey-patch to make merchant items clickable in landscape.
     * EventBus event should be used in couple with this flag.
     * God forgive me for this.
     */
    @State
    boolean isResumed = false;

    @Override
    protected DtlMerchantsHostPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlMerchantsHostPresenter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showMaster();
        tryShowSlave();
    }

    @Override
    public void onResume() {
        super.onResume();
        showDtlFilters();
        isResumed = true;
    }

    @Override
    public void onPause() {
        isResumed = false;
        super.onPause();
    }

    @Override
    public boolean isFragmentResumed() {
        return isResumed;
    }

    private void showMaster() {
        Fragment merchantsTabsFragment = getChildFragmentManager().findFragmentById(R.id.dtl_master_container);
        if (merchantsTabsFragment != null && merchantsTabsFragment.getClass().getName()
                .equals(Route.DTL_MERCHANTS_TABS.getClazzName())) return;
        router.moveTo(Route.DTL_MERCHANTS_TABS, NavigationConfigBuilder.forFragment()
                .containerId(R.id.dtl_master_container)
                .backStackEnabled(false)
                .fragmentManager(getChildFragmentManager())
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
                    .data(new DtlMapBundle(true)) // TODO : remove this bundle in favor of fragment arg
                    .build());
            landscapeSlave.setVisibility(View.VISIBLE);
        } else {
            removeDetails();
            landscapeSlave.setVisibility(View.GONE);
        }
    }

    private void showDtlFilters() {
        Fragment filtersFragment = getFragmentManager().findFragmentById(R.id.container_filters);
        if (filtersFragment != null && filtersFragment.getClass().getName()
                .equals(Route.DTL_FILTERS.getClazzName())) return;

        router.moveTo(Route.DTL_FILTERS, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .containerId(R.id.container_filters)
                .fragmentManager(getParentFragment().getFragmentManager())
                .build());
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
                // TODO : remove 'slave' parameter in favor of navigation config param
                .data(new DtlMerchantDetailsBundle(merchantId, true))
                .build());
    }
}
