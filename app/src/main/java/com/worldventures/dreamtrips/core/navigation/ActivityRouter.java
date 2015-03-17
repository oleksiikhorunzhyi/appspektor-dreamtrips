package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.techery.spares.ui.routing.ActivityBoundRouter;
import com.worldventures.dreamtrips.core.model.SuccessStory;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.view.activity.BookItActivity;
import com.worldventures.dreamtrips.view.activity.BucketListEditActivity;
import com.worldventures.dreamtrips.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.view.activity.DetailTripActivity;
import com.worldventures.dreamtrips.view.activity.EnrollActivity;
import com.worldventures.dreamtrips.view.activity.FBPickPhotoActivity;
import com.worldventures.dreamtrips.view.activity.FullScreenPhotoActivity;
import com.worldventures.dreamtrips.view.activity.FullScreenTripImageActivity;
import com.worldventures.dreamtrips.view.activity.LoginActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.activity.ShareActivity;
import com.worldventures.dreamtrips.view.activity.SimpleStreamPlayerActivity;
import com.worldventures.dreamtrips.view.activity.SuccessStoryDetailsActivity;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.view.fragment.TripImagesListFragment;

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

    public void openEnroll() {
        startActivity(EnrollActivity.class);
    }

    public void openFullScreenPhoto(int position, TripImagesListFragment.Type type) {
        Bundle bundle = new Bundle();
        bundle.putInt(FullScreenPhotoActivity.EXTRA_POSITION, position);
        bundle.putSerializable(FullScreenPhotoActivity.EXTRA_TYPE, type);
        startActivity(FullScreenPhotoActivity.class, bundle);
    }

    public void open360Activity(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(SimpleStreamPlayerActivity.EXTRA_URL, url);
        startActivity(SimpleStreamPlayerActivity.class, bundle);
    }

    public void openFullScreenTrip(List<Object> photoList, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FullScreenTripImageActivity.EXTRA_PHOTOS_LIST, new ArrayList<>(photoList));
        bundle.putSerializable(FullScreenTripImageActivity.EXTRA_POSITION, position);
        startActivity(FullScreenTripImageActivity.class, bundle);
    }

    public void openBookItActivity(int tripId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BookItActivity.EXTRA_TRIP_ID, tripId);
        startActivity(BookItActivity.class, bundle);
    }

    public void openBucketListEditActivity(BucketTabsFragment.Type type, State state) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BucketListEditActivity.EXTRA_TYPE, type);
        bundle.putSerializable(BucketListEditActivity.EXTRA_STATE, state);
        startActivity(BucketListEditActivity.class, bundle);
    }

    public void openTripDetails(Trip trip) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DetailTripActivity.EXTRA_TRIP, trip);
        startActivity(DetailTripActivity.class, bundle);
    }

    public void openFacebookPhoto(Fragment fm) {
        startForResult(fm, FBPickPhotoActivity.class, FBPickPhotoActivity.REQUEST_CODE_PICK_FB_PHOTO);
    }

    public void openShareFacebook(String imageUrl, String shareLink, String text) {
        if (imageUrl == null) imageUrl = "";
        if (shareLink == null) shareLink = "";
        if (text == null) text = "";
        Bundle bundle = new Bundle();
        bundle.putSerializable(ShareActivity.BUNDLE_IMAGE_URL, imageUrl);
        bundle.putSerializable(ShareActivity.BUNDLE_SHARE_URL, shareLink);
        bundle.putSerializable(ShareActivity.BUNDLE_TEXT, text);
        bundle.putSerializable(ShareActivity.BUNDLE_SHARE_TYPE, ShareActivity.FB);
        startActivity(ShareActivity.class, bundle);
    }

    public void openShareTwitter(String imageUrl, String shareLink, String text) {
        if (imageUrl == null) imageUrl = "";
        if (shareLink == null) shareLink = "";
        if (text == null) text = "";
        Bundle bundle = new Bundle();
        bundle.putSerializable(ShareActivity.BUNDLE_IMAGE_URL, imageUrl);
        bundle.putSerializable(ShareActivity.BUNDLE_SHARE_URL, shareLink);
        bundle.putSerializable(ShareActivity.BUNDLE_TEXT, text);
        bundle.putSerializable(ShareActivity.BUNDLE_SHARE_TYPE, ShareActivity.TW);
        startActivity(ShareActivity.class, bundle);
    }


    public void openShare(Intent share) {
        startActivityIntent(share);
    }

    public void openSuccessStoryDetails(SuccessStory successStory) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SuccessStoryDetailsActivity.BUNDLE_STORY, successStory);
        startActivity(SuccessStoryDetailsActivity.class, bundle);

    }
}
