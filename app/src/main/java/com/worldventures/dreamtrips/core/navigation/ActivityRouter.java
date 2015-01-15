package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;

import com.techery.spares.ui.routing.ActivityBoundRouter;
import com.worldventures.dreamtrips.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.view.activity.LoginActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;

public class ActivityRouter extends ActivityBoundRouter {


    public ActivityRouter(Activity activity) {
        super(activity);
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
