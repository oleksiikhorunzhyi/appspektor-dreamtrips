package com.techery.spares.ui.routing;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.techery.spares.ui.activity.BaseActivity;

public class BaseRouter {
    private final Activity activity;

    public BaseRouter(Activity activity) {
        this.activity = activity;
    }

    protected void startActivity(Class<? extends BaseActivity> activityClass) {
        startActivity(activityClass, null, -1);
    }

    protected void startActivityAndClearTop(Class<? extends BaseActivity> activityClass) {
        startActivity(activityClass, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    protected void startActivity(Class<? extends BaseActivity> activityClass, int flags) {
        startActivity(activityClass, null, flags);
    }

    protected void startActivity(Class<? extends BaseActivity> activityClass, Bundle params, int flags) {
        Intent intent = new Intent(this.activity, activityClass);
        
        if (params != null) {
            intent.putExtras(params);
        }

        if (flags > 0) {
            intent.setFlags(flags);
        }

        startActivityIntent(intent);
    }

    protected void startActivityIntent(Intent intent) {
        this.activity.startActivity(intent);
    }

    protected void startService(Class<? extends Service> serviceClass) {
        startServiceIntent(new Intent(this.activity, serviceClass));
    }

    protected void startServiceIntent(Intent intent) {
        this.activity.startService(intent);
    }

    protected void finish() {
        activity.finish();
    }

    public Context getContext() {
        return this.activity;
    }

    protected void openUri(Uri uri) {
        Intent videoClient = new Intent(Intent.ACTION_VIEW);
        videoClient.setData(uri);
        startActivityIntent(videoClient);
    }

    protected void startNewAndFinishCurrentActivity(Class<? extends BaseActivity> activityClass) {
        startActivity(activityClass);
        finish();
    }
}
