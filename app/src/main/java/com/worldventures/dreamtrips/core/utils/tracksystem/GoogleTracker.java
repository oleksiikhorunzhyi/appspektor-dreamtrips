package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.Map;

public class GoogleTracker implements ITracker {

    private Tracker tracker;
    private WeakReference<Activity> activity;

    @Override
    public void onCreate(BaseActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public void onStart(Activity activity) {

    }

    @Override
    public void onStop(Activity activity) {

    }

    @Override
    public void onResume(Activity activity) {

    }

    @Override
    public void onPause(Activity activity) {

    }

    @Override
    public void trackEvent(String category, String action, Map<String, Object> data) {
        if (getTracker() != null) {
            getTracker().send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .build());
        }
    }

    private Tracker getTracker() {
        if (tracker == null && activity.get() != null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(activity.get()
                    .getApplicationContext());
            tracker = analytics.newTracker(BuildConfig.GoogleTrackingID);
        }
        return tracker;
    }
}
