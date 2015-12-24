package com.messenger.synchmechanism;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

public class ActivityWatcher implements Application.ActivityLifecycleCallbacks {
    private static final int TIMER_FOR_DISCONNECT = 2000;

    MessengerConnector connector;
    Handler handler;

    int visibleActivityCount;

    public ActivityWatcher(Context context, MessengerConnector connector) {
        this.connector = connector;
        this.handler = new Handler();
        this.visibleActivityCount = 0;

        ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        visibleActivityCount++;
        if (visibleActivityCount != 1) return;
        handler.post(connector::connect);
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        visibleActivityCount--;

        handler.postDelayed(() -> {
            if (visibleActivityCount == 0) {
                connector.disconnect();
            }
        }, TIMER_FOR_DISCONNECT);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
