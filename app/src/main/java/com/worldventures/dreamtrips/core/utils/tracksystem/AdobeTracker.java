package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import icepick.Icepick;
import icepick.State;

public class AdobeTracker extends ITracker{

    @Override
    public void onCreate(BaseActivity activity) {
        Config.setDebugLogging(true);
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
        Analytics.trackAction(action, data);
    }

}
