package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.view.activity.LoginActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;

public class ActivityRouter {
    private static final String EXTRA_BUNDLE = "EXTRA_BUNDLE";
    BaseActivity currentActivity;//why not just context? because we will need to startActivityForResult

    public ActivityRouter(BaseActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public void openMain() {
        startActivity(MainActivity.class);
    }

    private void startActivity(Class<? extends BaseActivity> activityClass) {
        startActivity(activityClass, null);
    }

    private void startActivity(Class<? extends BaseActivity> activityClass, Bundle bundle) {
        Intent intent = new Intent(currentActivity, activityClass);
        if (bundle != null) {
            intent.putExtra(EXTRA_BUNDLE, bundle);
        }
        currentActivity.startActivity(intent);
    }

    public void openLogin() {
        startActivity(LoginActivity.class);
    }

    public void finish() {
        currentActivity.finish();
    }


    public void openCreatePhoto() {
        startActivity(CreatePhotoActivity.class);
    }

}
