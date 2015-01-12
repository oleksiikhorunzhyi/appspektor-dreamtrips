package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.view.activity.FullScreenPhotoActivity;
import com.worldventures.dreamtrips.view.activity.LoginActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;

import java.util.ArrayList;

public class ActivityRouter {
    public static final String EXTRA_BUNDLE = "EXTRA_BUNDLE";
    BaseActivity currentActivity;//why not just context? because we will need to startActivityForResult

    public ActivityRouter(BaseActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public void openMain() {
        startActivity(MainActivity.class);
    }

    private void startActivity(Class<? extends Activity> activityClass) {
        startActivity(activityClass, null);
    }

    private void startActivity(Class<? extends Activity> activityClass, Bundle bundle) {
        Intent intent = new Intent(currentActivity, activityClass);
        if (bundle != null) {
            intent.putExtra(EXTRA_BUNDLE, bundle);
        }
        currentActivity.startActivity(intent);
    }

    public void openCreatePhoto(Uri fileUri) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CreatePhotoActivity.EXTRA_FILE_URI, fileUri);
        startActivity(CreatePhotoActivity.class, bundle);
    }

    public void openLogin() {
        startActivity(LoginActivity.class);
    }

    public void finish() {
        currentActivity.finish();
    }


    public void openFullScreenPhoto(ArrayList<Photo> photoList, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FullScreenPhotoActivity.EXTRA_PHOTOS_LIST, photoList);
        bundle.putSerializable(FullScreenPhotoActivity.EXTRA_POSITION, position);
        startActivity(FullScreenPhotoActivity.class, bundle);
    }
}
