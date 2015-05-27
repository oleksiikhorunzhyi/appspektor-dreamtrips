package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import java.util.Map;

public class GoogleTracker implements ITracker {

    private Tracker tracker;

    @Override
    public void onCreate(BaseActivity activity) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(activity.getApplicationContext());
        tracker = analytics.newTracker("UA-XXXX-Y"); // Send hits to tracker id UA-XXXX-Y
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
    public void trackMemberAction(String action, Map<String, Object> data) {
        tracker.send(new HitBuilders.EventBuilder()
                .setAction(action)
                .build());
    }
}
