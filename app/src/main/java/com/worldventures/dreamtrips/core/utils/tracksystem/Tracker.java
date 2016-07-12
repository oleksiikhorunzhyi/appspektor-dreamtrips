package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import icepick.Icepick;
import icepick.State;
import timber.log.Timber;

public abstract class Tracker {

    @State
    protected HashMap headerData;

    public abstract String getKey();

    public abstract void onCreate(@Nullable Activity activity);

    public void onStart(@Nullable Activity activity) {
    }

    public void onStop(@Nullable Activity activity) {
    }

    public void onResume(@Nullable Activity activity) {
    }

    public void onPause(@Nullable Activity activity) {
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

    /**
     * Performs null check and warns log if activity is null.
     * @param activity instance to check
     * @return true if activity is null
     */
    protected boolean checkNullAndWarn(@Nullable Activity activity) {
        if (activity == null) {
            Timber.e(this.getClass().getName() + " lifecycle method: activity got weak");
        }
        return activity == null;
    }
}
