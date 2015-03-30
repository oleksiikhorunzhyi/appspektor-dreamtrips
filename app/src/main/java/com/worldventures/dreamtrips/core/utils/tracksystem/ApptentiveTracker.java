package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;

import com.apptentive.android.sdk.Apptentive;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import java.util.Map;

public class ApptentiveTracker implements ITracker {

    private Activity activity;

    @Override
    public void onCreate(BaseActivity activity) {

    }

    public void onStart(Activity activity) {
        this.activity = activity;
        Apptentive.onStart(activity);
    }

    public void onStop(Activity activity) {
        Apptentive.onStop(activity);
        activity = null;
    }


    @Override
    public void onResume(Activity activity) {

    }

    @Override
    public void onPause(Activity activity) {

    }

    @Override
    public void trackMemberAction(String action, Map<String, Object> data) {
        Apptentive.engage(activity, action, data);
    }
}
