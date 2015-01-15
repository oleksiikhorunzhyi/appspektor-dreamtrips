package com.worldventures.dreamtrips.presentation;


import com.worldventures.dreamtrips.core.SessionManager;

import javax.inject.Inject;

public class LaunchActivityPresentation extends BasePresentation<BasePresentation.View> {

    @Inject
    protected SessionManager sessionManager;

    public LaunchActivityPresentation(View view) {
        super(view);
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
