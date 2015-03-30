package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;

import com.apptentive.android.sdk.Apptentive;
import com.apptentive.android.sdk.Log;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import java.util.Map;

public class ApptentiveTracker implements ITracker {

    private static final String TAG = ApptentiveTracker.class.getSimpleName();
    private Activity activity;

    @Override
    public void onCreate(BaseActivity activity) {
        Log.v(TAG, "onCreate");
    }

    public void onStart(Activity activity) {
        this.activity = activity;
        Apptentive.onStart(activity);
    }

    public void onStop(Activity activity) {
        Apptentive.onStop(activity);
    }


    @Override
    public void onResume(Activity activity) {
        Log.v(TAG, "onResume");

    }

    @Override
    public void onPause(Activity activity) {
        Log.v(TAG, "onPause");
    }

    @Override
    public void trackMemberAction(String action, Map<String, Object> data) {
        Apptentive.engage(activity, action, data);
    }
}
