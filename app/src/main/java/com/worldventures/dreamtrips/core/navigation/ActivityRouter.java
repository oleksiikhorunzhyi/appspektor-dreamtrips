package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.content.Intent;

import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.activity.LoginActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;

public class ActivityRouter {
    BaseActivity currentActivity;//why not just context? because we will need to startActivityForResult

    public ActivityRouter(BaseActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public void openMain() {
        startActivity(MainActivity.class);
    }

    private void startActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(currentActivity, activityClass);
        currentActivity.startActivity(intent);
    }

    public void openLogin() {
        startActivity(LoginActivity.class);
    }

    public void finish() {
        currentActivity.finish();
    }

}
