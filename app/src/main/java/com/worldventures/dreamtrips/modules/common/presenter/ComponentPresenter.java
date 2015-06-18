package com.worldventures.dreamtrips.modules.common.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.component.ComponentDescription;

public class ComponentPresenter extends Presenter<Presenter.View> {

    public static final String COMPONENT = "component";
    public static final String COMPONENT_EXTRA = "component_extra";

    private ComponentDescription componentDescription;
    private Bundle componentExtras;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        fragmentCompass.replace(componentDescription, componentExtras);
    }

    public int getTitle() {
        return componentDescription.getToolbarTitle();
    }

    public ComponentPresenter(Bundle args) {
        componentDescription = args.getParcelable(COMPONENT);
        componentExtras = args.getBundle(COMPONENT_EXTRA);
    }
}
