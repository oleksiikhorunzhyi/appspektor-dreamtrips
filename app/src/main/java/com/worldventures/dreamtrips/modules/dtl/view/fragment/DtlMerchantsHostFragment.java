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
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantsBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantsMapBundle;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMerchantsHostPresenter;

import butterknife.InjectView;

@Layout(R.layout.fragment_dtl_merchants_host)
public class DtlMerchantsHostFragment
        extends BaseFragmentWithArgs<DtlMerchantsHostPresenter, MerchantsBundle>
        implements DtlMerchantsHostPresenter.View {

    @InjectView(R.id.dtl_landscape_slave_container)
    View landscapeSlave;

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

    private void showMaster() {
        Fragment merchantsTabsFragment = getChildFragmentManager().findFragmentById(R.id.dtl_master_container);
        if (merchantsTabsFragment != null && merchantsTabsFragment.getClass().getName()
                .equals(Route.DTL_MERCHANTS_LIST.getClazzName())) return;
        router.moveTo(Route.DTL_MERCHANTS_LIST, NavigationConfigBuilder.forFragment()
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
                    .data(new MerchantsMapBundle(getArgs().getLocation(), true))
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
    public void showDetails(DtlMerchant merchant) {
        removeDetails();
        router.moveTo(Route.DTL_MERCHANT_DETAILS, NavigationConfigBuilder.forFragment()
                .containerId(R.id.dtl_landscape_slave_container)
                .backStackEnabled(true)
                .fragmentManager(getChildFragmentManager())
                .data(new MerchantDetailsBundle(merchant, true))
                .build());
    }
}
