package com.worldventures.dreamtrips.modules.common.presenter;

import android.content.res.Configuration;
import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;

public class ComponentPresenter extends ActivityPresenter<ComponentPresenter.View> {

    public static final String ROUTE = "route";

    public static final String COMPONENT_EXTRA = "component_extras";
    public static final String COMPONENT_TOOLBAR_CONFIG = "component_toolbar";
    public static final String EXTRA_DATA = "EXTRA_DATA";


    private Bundle args;
    private Route route;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            fragmentCompass.replace(route, args);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        activity.recreate();
    }

    public int getTitle() {
        if (route != null) {
            return route.getTitleRes();
        } else {
            return 0;
        }
    }

    public ComponentPresenter(Bundle args) {
        route = (Route) args.getSerializable(ROUTE);
        this.args = args;
    }

    @Override
    protected boolean canShowTermsDialog() {
        return route != Route.LOGIN && super.canShowTermsDialog();
    }

    public interface View extends ActivityPresenter.View {
    }
}
