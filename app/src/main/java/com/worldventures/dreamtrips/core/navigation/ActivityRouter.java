package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.techery.spares.ui.routing.BaseRouter;
import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.view.activity.LoginActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;

public class ActivityRouter extends BaseRouter {
    private static final String EXTRA_BUNDLE = "EXTRA_BUNDLE";

    public ActivityRouter(Activity activity) {
        super(activity);
    }

    private void startActivity(Class<? extends BaseActivity> activityClass, Bundle bundle) {
        Intent intent = new Intent(getContext(), activityClass);
        if (bundle != null) {
            intent.putExtra(EXTRA_BUNDLE, bundle);
        }
        getContext().startActivity(intent);
    }

    public void openMain() {
        startActivity(MainActivity.class);
    }

    public void openLogin() {
        startActivity(LoginActivity.class);
    }

    public void openCreatePhoto() {
        startActivity(CreatePhotoActivity.class);
    }

}
