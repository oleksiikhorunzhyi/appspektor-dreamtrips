package com.worldventures.dreamtrips.core.navigation;

import android.content.Intent;

import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.activity.LoginActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;

public class ActivityCompass {
    BaseActivity baseActivity;//why not just context? because we will need to startActivityForResult

    public ActivityCompass(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public void openMain() {
        Intent intent = new Intent(baseActivity, MainActivity.class);
        baseActivity.startActivity(intent);
    }

    public void openLogin() {
        Intent intent = new Intent(baseActivity, LoginActivity.class);
        baseActivity.startActivity(intent);
    }

    public void finish() {
        baseActivity.finish();
    }

}
