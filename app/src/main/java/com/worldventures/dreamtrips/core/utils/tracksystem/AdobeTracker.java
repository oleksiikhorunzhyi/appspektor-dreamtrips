package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import java.util.Map;

import timber.log.Timber;

public class AdobeTracker implements ITracker {

    @Override
    public void onCreate(BaseActivity activity) {
        Config.setDebugLogging(true);
        Config.setContext(activity.getApplicationContext());
    }

    @Override
    public void onStart(Activity activity) {
        Timber.v("onStart");

    }

    @Override
    public void onStop(Activity activity) {
        Timber.v("onStop");
    }

    public void onResume(Activity activity) {
        Config.collectLifecycleData();
    }

    public void onPause(Activity activity) {
        Config.pauseCollectingLifecycleData();
    }

    @Override
    public void trackMemberAction(String action, Map<String, Object> data) {
        Analytics.trackAction(action, data);
    }
}
