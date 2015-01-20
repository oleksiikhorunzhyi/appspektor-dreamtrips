package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.techery.spares.ui.routing.ActivityBoundRouter;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.view.activity.DetailTripActivity;
import com.worldventures.dreamtrips.view.activity.FBPickPhotoActivity;
import com.worldventures.dreamtrips.view.activity.FullScreenPhotoActivity;
import com.worldventures.dreamtrips.view.activity.LoginActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityRouter extends ActivityBoundRouter {

    public ActivityRouter(Activity activity) {
        super(activity);
    }

    public void openMain() {
        startActivity(MainActivity.class);
    }

    public void openCreatePhoto(Fragment fm, Uri fileUri) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CreatePhotoActivity.EXTRA_FILE_URI, fileUri);
        startForResult(fm, CreatePhotoActivity.class, CreatePhotoActivity.REQUEST_CODE_CREATE_PHOTO, bundle);
    }

    public void openLogin() {
        startActivity(LoginActivity.class);
    }


    public void openFullScreenPhoto(List<Object> photoList, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FullScreenPhotoActivity.EXTRA_PHOTOS_LIST, new ArrayList<>(photoList));
        bundle.putSerializable(FullScreenPhotoActivity.EXTRA_POSITION, position);
        startActivity(FullScreenPhotoActivity.class, bundle);
    }

    public void openTripDetails(Trip trip) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DetailTripActivity.EXTRA_TRIP, trip);
        startActivity(DetailTripActivity.class, bundle);
    }

    public void openFacebookPhoto(Fragment fm) {
        startForResult(fm, FBPickPhotoActivity.class, FBPickPhotoActivity.REQUEST_CODE_PICK_FB_PHOTO);
    }

}
