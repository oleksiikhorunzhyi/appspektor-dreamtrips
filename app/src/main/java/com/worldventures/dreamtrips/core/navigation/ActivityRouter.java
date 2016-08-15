package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.webkit.MimeTypeMap;

import com.techery.spares.ui.routing.ActivityBoundRouter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.Player360Activity;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ActivityRouter extends ActivityBoundRouter {

    @StringDef({LAUNCH_LOGIN , LAUNCH_SPLASH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LaunchType{}

    public static final String LAUNCH_LOGIN = LaunchActivity.LOGIN;
    public static final String LAUNCH_SPLASH = LaunchActivity.SPLASH;

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

    public void openLaunch(@LaunchType String type) {
        Bundle bundle = new Bundle();
        bundle.putString(LaunchActivity.EXTRA_TYPE, type);
        startActivity(LaunchActivity.class, bundle, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    /**
     * {@link ActivityNotFoundException} would be thrown
     * if there was no Activity found to run the given Intent
     *
     * @param url audio file url
     */
    public void openDeviceAudioPlayerForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        intent.setDataAndType(Uri.parse(url), mimeType);
        startActivity(Intent.createChooser(intent, getContext().getString(R.string.complete_action_with)));
    }

    /**
     * {@link ActivityNotFoundException} would be thrown
     * if there was no Activity found to run the given Intent
     *
     * @param path audio file url
     */
    public void openDeviceAudioPlayerForFile(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(path);
        intent.setDataAndType(Uri.fromFile(file), "audio/*");
        startActivity(Intent.createChooser(intent, getContext().getString(R.string.complete_action_with)));
    }
}
