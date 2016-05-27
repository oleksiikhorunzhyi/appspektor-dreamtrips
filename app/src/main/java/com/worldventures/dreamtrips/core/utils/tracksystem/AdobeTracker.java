package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class AdobeTracker extends ITracker {

    private static final String DEFAULT_PREFIX = "dta:";

    private static final String CHANNEL_KEY = "channel";
    private static final String CHANNEL_VALUE = "App:Dreamtrips";

    @Override
    public void onCreate(BaseActivity activity) {
        Config.setDebugLogging(BuildConfig.DEBUG);
        Config.setContext(activity.getApplicationContext());
    }

    @Override
    public void onResume(Activity activity) {
        Config.collectLifecycleData();
    }

    @Override
    public void onPause(Activity activity) {
        Config.pauseCollectingLifecycleData();
    }

    @Override
    public void trackEvent(String category, String action, Map<String, Object> data) {
        if (data == null) data = new HashMap<>();
        if (headerData != null) data.putAll(headerData);

        data.put(CHANNEL_KEY, CHANNEL_VALUE);

        Analytics.trackState(prepareAction(action), data);
    }

    private String prepareAction(String action) {
        return DEFAULT_PREFIX + action;
    }
}
