package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;

import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import java.util.Map;

public interface ITracker {

    void onCreate(BaseActivity activity);

    void onStart(Activity activity);

    void onStop(Activity activity);

    void onResume(Activity activity);

    void onPause(Activity activity);

    void trackMemberAction(String action, Map<String, Object> data);

    void trackEvent(String category, String action, Map<String, Object> data);
}
