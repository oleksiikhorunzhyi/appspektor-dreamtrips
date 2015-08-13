package com.worldventures.dreamtrips.modules.common.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;

public class ComponentPresenter extends Presenter<Presenter.View> {

    public static final String COMPONENT = "component";
    public static final String ROUTE = "route";
    public static final String COMPONENT_EXTRA = "component_extra";

    private ComponentDescription componentDescription;
    private Bundle componentExtras;
    private Route route;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (componentDescription != null)
                fragmentCompass.replace(componentDescription, componentExtras);
            else if (route != null) {
                fragmentCompass.replace(route, componentExtras);
            }
        }
    }

    public int getTitle() {
        if (componentDescription != null)
            return componentDescription.getToolbarTitle();
        else if (route != null) {
            return route.getTitleRes();
        }

        return 0;
    }

    public ComponentPresenter(Bundle args) {
        componentDescription = args.getParcelable(COMPONENT);
        route = (Route) args.getSerializable(ROUTE);
        componentExtras = args.getBundle(COMPONENT_EXTRA);
    }
}
