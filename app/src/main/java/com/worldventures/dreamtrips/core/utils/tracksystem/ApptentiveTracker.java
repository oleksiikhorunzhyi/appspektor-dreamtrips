package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.apptentive.android.sdk.Apptentive;

import java.lang.ref.WeakReference;
import java.util.Map;

import timber.log.Timber;

public class ApptentiveTracker extends Tracker {

    public static final String TRACKER_KEY = "apptentive_tracker";

    private WeakReference<Activity> activity;

    @Override
    public String getKey() {
        return TRACKER_KEY;
    }

    @Override
    public void onCreate(@Nullable Activity activity) {
        Timber.v("onCreate");
    }

    public void onStart(@Nullable Activity activity) {
        if (checkNullAndWarn(activity)) return;
        this.activity = new WeakReference<>(activity);
        Apptentive.onStart(activity);
    }

    public void onStop(@Nullable Activity activity) {
        if (checkNullAndWarn(activity)) return;
        Apptentive.onStop(activity);
    }

    @Override
    public void onResume(@Nullable Activity activity) {
        Timber.v("onResume");
    }

    @Override
    public void onPause(@Nullable Activity activity) {
        Timber.v("onPause");
    }

    @Override
    public void trackEvent(String category, String action, Map<String, Object> data) {
        if (activity == null) return;

        Activity activity = this.activity.get();
        if (activity != null) {
            Apptentive.engage(activity, TextUtils.join(":", new String[]{category, action}));
        }
    }
}
