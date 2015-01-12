package com.worldventures.dreamtrips.view.presentation;

import com.worldventures.dreamtrips.view.activity.Injector;

public class BaseActivityPresentation extends BasePresentation {


    public BaseActivityPresentation(IInformView view, Injector injector) {
        super(view, injector);
    }

    public void pop() {
        fragmentCompass.pop();
    }


    public void onCreate() {
        try {
            dataManager.setCurrentUser(sessionManager.getCurrentUser());
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void onPause() {
        sessionManager.saveCurrentUser(dataManager.getCurrentUser());
    }

}
