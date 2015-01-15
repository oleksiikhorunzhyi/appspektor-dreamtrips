package com.worldventures.dreamtrips.view.presentation;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.IllegalCuurentUserState;
import com.worldventures.dreamtrips.core.SessionManager;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;

import javax.inject.Inject;

public class BasePresentation {

    @Inject
    protected DataManager dataManager;

    @Inject
    protected FragmentCompass fragmentCompass;

    @Inject
    protected ActivityRouter activityRouter;

    @Inject
    protected SessionManager sessionManager;

    protected IInformView view;

    public BasePresentation(IInformView view, Injector injector) {
        injector.inject(this);
    }

    public void handleError(Exception ex) {
        if (ex instanceof IllegalCuurentUserState) {
            sessionManager.logoutUser();
            activityRouter.finish();
            activityRouter.openLogin();
        }
    }

}
