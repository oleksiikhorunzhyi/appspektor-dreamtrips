package com.worldventures.dreamtrips.presentation;


import com.worldventures.dreamtrips.core.session.AppSessionHolder;


import javax.inject.Inject;

public class LaunchActivityPresentation extends BasePresentation<BasePresentation.View> {

    @Inject
    protected AppSessionHolder appSessionHolder;

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
