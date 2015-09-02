package com.worldventures.dreamtrips.modules.common.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;

public class ComponentPresenter extends Presenter<ComponentPresenter.View> {

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

    public interface View extends Presenter.View {
    }
}
