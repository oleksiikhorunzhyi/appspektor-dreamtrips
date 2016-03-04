package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;
import android.text.TextUtils;

import com.apptentive.android.sdk.Apptentive;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.Map;

import timber.log.Timber;

public class ApptentiveTracker extends ITracker {

    private WeakReference<Activity> activity;

    @Override
    public void onCreate(BaseActivity activity) {
        Timber.v("onCreate");
    }

    public void onStart(Activity activity) {
        this.activity = new WeakReference<>(activity);
        Apptentive.onStart(activity);
    }

    public void onStop(Activity activity) {
        Apptentive.onStop(activity);
    }


    @Override
    public void onResume(Activity activity) {
        Timber.v("onResume");
    }

    @Override
    public void onPause(Activity activity) {
        Timber.v("onPause");
    }

    @Override
    public void trackEvent(String category, String action, Map<String, Object> data) {
        if (activity == null) return;
        //
        Activity activity = this.activity.get();
        if (activity != null) {
            Apptentive.engage(activity, TextUtils.join(":", new String[]{category, action}));
        }
    }

}
