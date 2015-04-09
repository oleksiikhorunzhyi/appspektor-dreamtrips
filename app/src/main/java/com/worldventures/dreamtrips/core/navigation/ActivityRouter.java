package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.techery.spares.ui.routing.ActivityBoundRouter;
import com.worldventures.dreamtrips.modules.auth.view.LoginActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.SimpleStreamPlayerActivity;
import com.worldventures.dreamtrips.modules.facebook.view.activity.FacebookPickPhotoActivity;
import com.worldventures.dreamtrips.modules.infopages.view.activity.EnrollActivity;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.view.activity.SuccessStoryDetailsActivity;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.view.activity.BookItActivity;
import com.worldventures.dreamtrips.modules.trips.view.activity.DetailTripActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.FullScreenPhotoActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.FullScreenTripImageActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

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
        finish();
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

    public void openBookItActivity(String tripId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BookItActivity.EXTRA_TRIP_ID, tripId);
        startActivity(BookItActivity.class, bundle);
    }

    public void openBucketListPopularActivity(BucketTabsFragment.Type type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BucketActivity.EXTRA_TYPE, type);
        bundle.putSerializable(BucketActivity.EXTRA_STATE, Route.POPULAR_TAB_BUCKER);
        startActivity(BucketActivity.class, bundle);
    }

    public void openBucketItemEditActivity(Bundle bundle) {
        bundle.putSerializable(BucketActivity.EXTRA_STATE, Route.BUCKET_EDIT);
        startActivity(BucketActivity.class, bundle);
    }

    public void openBucketItemDetails(Bundle bundle) {
        bundle.putSerializable(BucketActivity.EXTRA_STATE, Route.DETAIL_BUCKET);
        startActivity(BucketActivity.class, bundle);
    }

    public void openTripDetails(TripModel trip) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DetailTripActivity.EXTRA_TRIP, trip);
        startActivity(DetailTripActivity.class, bundle);
    }

    public void openFacebookPhoto(Fragment fm) {
        startForResult(fm, FacebookPickPhotoActivity.class, FacebookPickPhotoActivity.REQUEST_CODE_PICK_FB_PHOTO);
    }

    public void openShareFacebook(String imageUrl, String shareLink, String text) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ShareActivity.BUNDLE_IMAGE_URL, imageUrl);
        bundle.putSerializable(ShareActivity.BUNDLE_SHARE_URL, shareLink);
        bundle.putSerializable(ShareActivity.BUNDLE_TEXT, text);
        bundle.putSerializable(ShareActivity.BUNDLE_SHARE_TYPE, ShareActivity.FB);
        startActivity(ShareActivity.class, bundle);
    }

    public void openShareTwitter(String imageUrl, String shareLink, String text) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ShareActivity.BUNDLE_IMAGE_URL, imageUrl);
        bundle.putSerializable(ShareActivity.BUNDLE_SHARE_URL, shareLink);
        bundle.putSerializable(ShareActivity.BUNDLE_TEXT, text);
        bundle.putSerializable(ShareActivity.BUNDLE_SHARE_TYPE, ShareActivity.TW);
        startActivity(ShareActivity.class, bundle);
    }

    public void openSuccessStoryDetails(SuccessStory successStory) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SuccessStoryDetailsActivity.BUNDLE_STORY, successStory);
        startActivity(SuccessStoryDetailsActivity.class, bundle);
    }
}
