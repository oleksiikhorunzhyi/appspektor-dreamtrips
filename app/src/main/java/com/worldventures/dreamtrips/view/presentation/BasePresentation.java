package com.worldventures.dreamtrips.view.presentation;

import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.SessionManager;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.view.activity.Injector;

import javax.inject.Inject;

public class BasePresentation {

    @Inject
    protected DataManager dataManager;

    @Inject
    protected FragmentCompass fragmentCompass;

    @Inject
    protected ActivityRouter activityRouter;

    public BasePresentation(Injector injector) {
        injector.inject(this);
    }
}
