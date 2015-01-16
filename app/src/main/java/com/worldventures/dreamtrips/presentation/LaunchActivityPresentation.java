package com.worldventures.dreamtrips.presentation;


import com.worldventures.dreamtrips.core.session.AppSessionHolder;

import org.robobinding.annotation.PresentationModel;

import javax.inject.Inject;

@PresentationModel
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
