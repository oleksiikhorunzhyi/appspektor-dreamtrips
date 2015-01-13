package com.worldventures.dreamtrips.view.presentation;


import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.SessionManager;

import javax.inject.Inject;

public class LaunchActivityPresentation extends BasePresentation {

    @Inject
    protected SessionManager sessionManager;

    public LaunchActivityPresentation(IInformView view, Injector objectGraph) {
        super(view, objectGraph);
    }

    public void onCreate() {

        if (sessionManager.isUserLoggedIn()) {
            activityRouter.openMain();
        } else {
            activityRouter.openLogin();
        }

        activityRouter.finish();
    }

}
