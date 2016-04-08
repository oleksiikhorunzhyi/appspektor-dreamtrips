package com.worldventures.dreamtrips.modules.dtl.helper;

import android.app.Activity;
import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;

public class DtlEnrollWizard {

    private Router router;
    private RouteCreator<DtlTransaction> routeCreator;

    public DtlEnrollWizard(Router router, RouteCreator<DtlTransaction> routeCreator) {
        this.router = router;
        this.routeCreator = routeCreator;
    }

    public void clearAndProceed(FragmentManager fragmentManager, DtlTransaction dtlTransaction,
                                MerchantIdBundle bundle) {
        showNext(fragmentManager, dtlTransaction, bundle, true);
    }

    public void proceed(FragmentManager fragmentManager, DtlTransaction dtlTransaction,
                        MerchantIdBundle bundle) {
        showNext(fragmentManager, dtlTransaction, bundle, false);
    }

    private void showNext(FragmentManager fragmentManager, DtlTransaction dtlTransaction,
                          MerchantIdBundle bundle, boolean clearBackStack) {
        Route route = routeCreator.createRoute(dtlTransaction);
        router.moveTo(route,
                NavigationConfigBuilder.forFragment()
                        .containerId(R.id.container_main)
                        .backStackEnabled(true)
                        .data(bundle)
                        .clearBackStack(clearBackStack)
                        .fragmentManager(fragmentManager)
                        .build());
    }

    public void finish(Activity activity) {
        activity.finish();
    }
}
