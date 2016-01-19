package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.Map;

public class GoogleTracker extends ITracker {

    private Tracker tracker;
    private WeakReference<Activity> activity;

    @Override
    public void onCreate(BaseActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public void trackEvent(String category, String action, Map<String, Object> data) {
        Tracker tracker = getTracker();
        if (tracker != null) {
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .build());
        }
    }

    @Nullable
    private Tracker getTracker() {
        if (tracker == null && activity == null) {
            Crashlytics.logException(new IllegalStateException("GoogleTracker: Activity's weak ref is null where it should not be"));
            return null;
        }
        //
        if (tracker == null && activity.get() != null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(activity.get()
                    .getApplicationContext());
            tracker = analytics.newTracker(BuildConfig.GoogleTrackingID);
        }
        return tracker;
    }
}
