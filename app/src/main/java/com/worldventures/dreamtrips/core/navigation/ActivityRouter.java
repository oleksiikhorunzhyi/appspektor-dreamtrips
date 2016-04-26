package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.techery.spares.ui.routing.ActivityBoundRouter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.Player360Activity;

public class ActivityRouter extends ActivityBoundRouter {

    public ActivityRouter(Activity activity) {
        super(activity);
    }

    public void openMain() {
        startActivity(MainActivity.class);
    }

    public void openMainWithComponent(String key) {
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.COMPONENT_KEY, key);
        startActivity(MainActivity.class, bundle);
    }

    public void openLaunch() {
        startActivity(LaunchActivity.class);
    }

    public void open360Activity(String url, String title) {
        Bundle bundle = new Bundle();
        bundle.putString(Player360Activity.EXTRA_URL, url);
        bundle.putString(Player360Activity.EXTRA_TITLE, title);
        startActivity(Player360Activity.class, bundle);
    }

    public void openDefaultShareIntent(Intent intent) {
        startActivityIntent(Intent.createChooser(intent, getActivity().getString(R.string.action_share)));
    }

    public void openComponentActivity(@NonNull Route route, @NonNull Bundle args) {
        args.putSerializable(ComponentPresenter.ROUTE, route);
        startActivityWithArgs(ComponentActivity.class, args);
    }

    public void openComponentActivity(@NonNull Route route, @NonNull Bundle args, int flags) {
        args.putSerializable(ComponentPresenter.ROUTE, route);
        startActivityWithArgs(ComponentActivity.class, args, flags);
    }

    public void startService(Class clazz) {
        super.startService(clazz);
    }
}
