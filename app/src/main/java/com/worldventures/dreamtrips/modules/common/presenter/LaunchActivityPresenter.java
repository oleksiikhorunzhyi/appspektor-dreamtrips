package com.worldventures.dreamtrips.modules.common.presenter;


public class LaunchActivityPresenter extends Presenter<Presenter.View> {

    public LaunchActivityPresenter(View view) {
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
