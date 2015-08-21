package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.techery.spares.ui.routing.ActivityBoundRouter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.modules.auth.view.LoginActivity;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.SimpleStreamPlayerActivity;
import com.worldventures.dreamtrips.modules.friends.view.activity.FriendSearchActivity;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.view.activity.EditTemplateActivity;
import com.worldventures.dreamtrips.modules.membership.view.activity.InviteTemplateSelectorActivity;
import com.worldventures.dreamtrips.modules.membership.view.activity.PreviewTemplateActivity;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;
import com.worldventures.dreamtrips.modules.profile.view.activity.FriendPrefsWrapperActivity;
import com.worldventures.dreamtrips.modules.profile.view.activity.ProfileActivity;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.view.activity.SuccessStoryDetailsActivity;
import com.worldventures.dreamtrips.modules.trips.view.activity.BookItActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.CreatePhotoActivity;

public class ActivityRouter extends ActivityBoundRouter {

    private FeatureManager featureManager;

    public ActivityRouter(Activity activity, FeatureManager featureManager) {
        super(activity);
        this.featureManager = featureManager;
    }

    public void openMain() {
        startActivity(MainActivity.class);
    }

    public void openLaunch() {
        startActivity(LaunchActivity.class);
    }

    public void openCreatePhoto(Fragment fm, Uri fileUri, String type) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CreatePhotoActivity.EXTRA_FILE_URI, fileUri);
        bundle.putString(CreatePhotoActivity.EXTRA_TYPE, type);
        startForResult(fm, CreatePhotoActivity.class, CreatePhotoActivity.REQUEST_CODE_CREATE_PHOTO, bundle);
    }

    public void openLogin() {
        startActivity(LoginActivity.class);
        finish();
    }

    public void open360Activity(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(SimpleStreamPlayerActivity.EXTRA_URL, url);
        startActivity(SimpleStreamPlayerActivity.class, bundle);
    }

    public void openUserProfile(User user) {
        if (featureManager.isUserInfoAvailable(user)) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(ProfileModule.EXTRA_USER, user);
            startActivity(ProfileActivity.class, bundle);
        }
    }

    public void openBookItActivity(String tripId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BookItActivity.EXTRA_TRIP_ID, tripId);
        startActivity(BookItActivity.class, bundle);
    }

    public void openPreviewActivity(String url) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(StaticInfoFragment.BundleUrlFragment.URL_EXTRA, url);
        startActivity(PreviewTemplateActivity.class, bundle);
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

    public void openDefaultShareIntent(Intent intent) {
        startActivityIntent(Intent.createChooser(intent, getActivity().getString(R.string.action_share)));
    }

    public void openSuccessStoryDetails(SuccessStory successStory) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SuccessStoryDetailsActivity.BUNDLE_STORY, successStory);
        startActivity(SuccessStoryDetailsActivity.class, bundle);
    }

    public void openFriendPrefs(User friend) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(FriendPrefsWrapperActivity.BUNDLE_FRIEND, friend);
        startActivity(FriendPrefsWrapperActivity.class, bundle);
    }

    public void openEditInviteActivity(InviteTemplate inviteTemplate) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EditTemplateActivity.BUNDLE_TEMPLATE, inviteTemplate);
        startActivity(EditTemplateActivity.class, bundle);
    }

    public void openSelectTemplateActivity() {
        startActivity(InviteTemplateSelectorActivity.class);
    }

    public void openComponentActivity(@NonNull Route route, @NonNull Bundle args) {
        args.putSerializable(ComponentPresenter.ROUTE, route);
        startActivityWithArgs(ComponentActivity.class, args);
    }

    public void openFriendsSearch() {
        startActivity(FriendSearchActivity.class);
    }

    public FeatureManager getFeatureManager() {
        return featureManager;
    }
}
