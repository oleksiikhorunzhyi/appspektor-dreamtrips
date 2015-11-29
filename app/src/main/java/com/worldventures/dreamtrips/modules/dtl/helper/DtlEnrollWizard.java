package com.worldventures.dreamtrips.modules.dtl.helper;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;

public class DtlEnrollWizard {

    private Router router;
    private RouteCreator<DtlTransaction> routeCreator;
    private DtlTransaction dtlTransaction;

    private Toolbar toolbar;

    public DtlEnrollWizard(Router router, RouteCreator<DtlTransaction> routeCreator) {
        this.router = router;
        this.routeCreator = routeCreator;
    }

    public void setDtlTransaction(DtlTransaction dtlTransaction) {
        this.dtlTransaction = dtlTransaction;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public void clearAndProceed(FragmentManager fragmentManager) {
        showNext(fragmentManager, true);
    }

    public void proceed(FragmentManager fragmentManager) {
        showNext(fragmentManager, false);
    }

    private void showNext(FragmentManager fragmentManager, boolean clearBackStack) {
        Route route = routeCreator.createRoute(dtlTransaction);
        toolbar.setTitle(route.getTitleRes());
        router.moveTo(route,
                NavigationConfigBuilder.forFragment()
                        .containerId(R.id.container_filters)
                        .clearBackStack(clearBackStack)
                        .fragmentManager(fragmentManager)
                        .build());

    }

    public void finish(Activity activity) {
        activity.finish();
    }
}
