package com.techery.spares.ui.routing;

import android.app.Activity;

import com.techery.spares.ui.activity.InjectingActivity;

public class ActivityBoundRouter extends BaseRouter {

    private final Activity activity;

    public ActivityBoundRouter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void finish() {
        activity.finish();
    }

    protected void startNewAndFinishCurrentActivity(Class<? extends InjectingActivity> activityClass) {
        startActivity(activityClass);
        finish();
    }


}
