package com.worldventures.dreamtrips.view.presentation;

import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.view.activity.Injector;

public class LoginActivityPresentation extends BasePresentation {

    public LoginActivityPresentation(Injector objectGraph) {
        super(objectGraph);
    }

    public void onCreate() {
        fragmentCompass.add(State.LOGIN);
    }
}
