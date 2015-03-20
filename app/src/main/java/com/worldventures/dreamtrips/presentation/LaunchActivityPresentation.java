package com.worldventures.dreamtrips.presentation;


public class LaunchActivityPresentation extends BasePresentation<BasePresentation.View> {

    public LaunchActivityPresentation(View view) {
        super(view);
    }

    public void onCreate() {

        if (appSessionHolder.get().isPresent()) {
            activityRouter.openMain();
        } else {
            activityRouter.openLogin();
        }

        activityRouter.finish();
    }

}
