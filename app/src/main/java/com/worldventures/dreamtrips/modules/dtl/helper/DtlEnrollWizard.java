package com.worldventures.dreamtrips.modules.dtl.helper;

import android.app.Activity;
import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;

public class DtlEnrollWizard {

    private Router router;
    private RouteCreator<DtlTransaction> routeCreator;

    public DtlEnrollWizard(Router router, RouteCreator<DtlTransaction> routeCreator) {
        this.router = router;
        this.routeCreator = routeCreator;
    }

    public void clearAndProceed(FragmentManager fragmentManager, DtlTransaction dtlTransaction, DtlMerchant DtlMerchant) {
        showNext(fragmentManager, dtlTransaction, DtlMerchant, true);
    }

    public void proceed(FragmentManager fragmentManager, DtlTransaction dtlTransaction, DtlMerchant DtlMerchant) {
        showNext(fragmentManager, dtlTransaction, DtlMerchant, false);
    }

    private void showNext(FragmentManager fragmentManager, DtlTransaction dtlTransaction, DtlMerchant DtlMerchant,
                          boolean clearBackStack) {
        Route route = routeCreator.createRoute(dtlTransaction);
        router.moveTo(route,
                NavigationConfigBuilder.forFragment()
                        .containerId(R.id.container_main)
                        .backStackEnabled(true)
                        .data(DtlMerchant)
                        .clearBackStack(clearBackStack)
                        .fragmentManager(fragmentManager)
                        .build());

    }

    public void finish(Activity activity) {
        activity.finish();
    }
}
