package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.worldventures.dreamtrips.BuildConfig;

import java.util.HashMap;
import java.util.Map;

public class AdobeTracker extends Tracker {

    public static final String TRACKER_KEY = "adobe_tracker";

    private static final String DEFAULT_PREFIX = "dta:";

    private static final String CHANNEL_KEY = "channel";
    private static final String CHANNEL_VALUE = "App:Dreamtrips";

    @Override
    public String getKey() {
        return TRACKER_KEY;
    }

    @Override
    public void onCreate(@Nullable Activity activity) {
        if (checkNullAndWarn(activity)) return;
        Config.setDebugLogging(BuildConfig.DEBUG);
        Config.setContext(activity.getApplicationContext());
    }

    @Override
    public void onResume(@Nullable Activity activity) {
        if (checkNullAndWarn(activity)) return;
        Config.collectLifecycleData();
    }

    @Override
    public void onPause(@Nullable Activity activity) {
        if (checkNullAndWarn(activity)) return;
        Config.pauseCollectingLifecycleData();
    }

    @Override
    public void trackEvent(String category, String action, Map<String, Object> data) {
        if (data == null) data = new HashMap<>();
        if (headerData != null) data.putAll(headerData);

        data.put(CHANNEL_KEY, CHANNEL_VALUE);

        Analytics.trackState(prepareAction(action), data);
        Analytics.trackAction(prepareAction(action), data);
    }

    private String prepareAction(String action) {
        return DEFAULT_PREFIX + action;
    }
}
