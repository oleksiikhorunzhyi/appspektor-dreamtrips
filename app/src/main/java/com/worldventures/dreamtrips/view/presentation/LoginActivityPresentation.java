package com.worldventures.dreamtrips.view.presentation;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.navigation.State;

public class LoginActivityPresentation extends BasePresentation {

    public LoginActivityPresentation(IInformView view, Injector objectGraph) {
        super(view, objectGraph);
    }

    public void onCreate() {
        fragmentCompass.add(State.LOGIN);
    }
}
