package com.worldventures.dreamtrips.view.presentation;


import com.worldventures.dreamtrips.view.activity.Injector;

public class LaunchActivityPresentation extends BasePresentation{

    public LaunchActivityPresentation(Injector objectGraph) {
        super(objectGraph);
    }

    public void onCreate(){
        if (sessionManager.isUserLoggedIn()) {
            activityCompass.openMain();
        } else {
            activityCompass.openLogin();
        }
    }

}
