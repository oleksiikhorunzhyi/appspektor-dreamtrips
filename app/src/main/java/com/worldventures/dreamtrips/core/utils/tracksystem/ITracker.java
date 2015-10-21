package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;
import android.os.Bundle;

import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import java.util.HashMap;
import java.util.Map;

import icepick.Icepick;
import icepick.State;

public abstract class ITracker {

    @State
    protected HashMap headerData;

    public abstract void onCreate(BaseActivity activity);

    public void onStart(Activity activity) {
    }

    public void onStop(Activity activity) {
    }

    public void onResume(Activity activity) {
    }

    public void onPause(Activity activity) {
    }

    public void setHeaderData(HashMap data) {
        headerData = data;
    }

    public void onSaveInstanceState(Bundle outState) {
        Icepick.saveInstanceState(this, outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    public abstract void trackEvent(String category, String action, Map<String, Object> data);

}
