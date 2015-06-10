package com.worldventures.dreamtrips.modules.profile.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.events.OpenMenuItemEvent;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.BucketListModule;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;
import com.worldventures.dreamtrips.modules.profile.api.GetProfileQuery;
import com.worldventures.dreamtrips.modules.profile.api.UploadAvatarCommand;
import com.worldventures.dreamtrips.modules.tripsimages.TripsImagesModule;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesTabsPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.io.File;

import javax.inject.Inject;

import retrofit.mime.TypedFile;

public class ProfilePresenter extends Presenter<ProfilePresenter.View> {

    @Inject
    protected Prefs prefs;

    @Inject
    protected SnappyRepository snappyRepository;

    @Inject
    protected RootComponentsProvider rootComponentsProvider;

    private User user;
    private boolean isCurrentUserProfile;

    private ImagePickCallback avatarCallback = (fragment, image, error) -> {
        if (image != null) {
            final File file = new File(image.getFileThumbnail());
            final TypedFile typedFile = new TypedFile("image/*", file);
            view.avatarProgressVisible(true);
            TrackingHelper.profileUploadStart(getUserId());
            doRequest(new UploadAvatarCommand(typedFile),
                    this::onSuccess);
        }
    };

    private ImagePickCallback coverCallback = (fragment, image, error) -> {
        if (image != null) {
            view.setCoverImage(Uri.fromFile(new File(image.getFileThumbnail())));

            UserSession userSession = this.appSessionHolder.get().get();
            User user = userSession.getUser();

            user.setCoverPath(image.getFileThumbnail());

            this.appSessionHolder.put(userSession);

            eventBus.post(new UpdateUserInfoEvent());
        }
    };

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.hideFriendRequest();
        if (view.getArguments() != null) {
            user = view.getArguments().getParcelable(ProfileModule.EXTRA_USER);
            isCurrentUserProfile = getUser().equals(user);
        } else {
            user = getUser();
            isCurrentUserProfile = true;
        }

        setUserProfileInfo();
        if (!isCurrentUserProfile) {
            setOtherUserProfile();
        } else {
            setCurrentUserProfile();
        }

        loadProfile();
    }

    public void onRefresh() {
        loadProfile();
    }

    private void setUserProfileInfo() {
        view.setUserName(user.getFullName());
        view.setDateOfBirth(DateTimeUtils.convertDateToString(user.getBirthDate(),
                DateFormat.getMediumDateFormat(context)));
        view.setUserId(user.getUsername());
        view.setLivesIn(user.getLocation());
        view.setFrom(user.getLocation());

        if (user.isGold())
            view.setGold();
        else if (user.isPlatinum())
            view.setPlatinum();
        else
            view.setMember();

        view.setAvatarImage(Uri.parse(user.getAvatar().getMedium()));
        view.setCoverImage(Uri.fromFile(new File(user.getCoverPath())));
        view.setRoviaBucks(user.getRoviaBucks());
        view.setDreamTripPoints(user.getDreamTripsPoints());
    }

    private void setOtherUserProfile() {
        view.hideAccountContent();
        view.showAddFriend();
        //TODO check has user sent a friend request
        view.showFriendRequest();
        //TODO add friend check
        boolean isFriend = false;
        view.setIsFriend(isFriend);
        //TODO load user profile
        //
        view.hideBalance();
    }

    private void setCurrentUserProfile() {
        TrackingHelper.profile(getUserId());
        view.showUpdateProfile();
        view.showBalance();
        view.showAccountContent();
    }

    private void loadProfile() {
        view.startLoading();
        doRequest(new GetProfileQuery(), this::onProfileLoaded);
    }

    private void onProfileLoaded(User user) {
        view.finishLoading();
        this.user = user;
        setUserProfileInfo();
        view.setTripImagesCount(user.getTripImagesCount());
        view.setBucketItemsCount(user.getBucketListItemsCount());
    }

    public boolean isCurrentUserProfile() {
        return isCurrentUserProfile;
    }

    @Override
    public void handleError(SpiceException error) {
        view.avatarProgressVisible(false);
        super.handleError(error);
    }

    public void openBucketList() {
        eventBus.post(new OpenMenuItemEvent(rootComponentsProvider
                .getComponentByKey(BucketListModule.BUCKETLIST)));
    }

    public void openTripImages() {
        Bundle args = new Bundle();
        args.putInt(TripImagesTabsPresenter.SELECTION_EXTRA, TripImagesListFragment.Type.MY_IMAGES.ordinal());
        eventBus.post(new OpenMenuItemEvent(rootComponentsProvider
                .getComponentByKey(TripsImagesModule.TRIP_IMAGES),
                args));
    }

    private void onSuccess(User obj) {
        TrackingHelper.profileUploadFinish(getUserId());
        UserSession userSession = appSessionHolder.get().get();
        User user = userSession.getUser();
        user.setAvatar(obj.getAvatar());

        appSessionHolder.put(userSession);
        view.setAvatarImage(Uri.parse(user.getAvatar().getMedium()));
        view.avatarProgressVisible(false);
        eventBus.post(new UpdateUserInfoEvent());
    }

    public void logout() {
        this.appSessionHolder.destroy();
        snappyRepository.clearAll();
        activityRouter.finish();
        activityRouter.openLogin();
    }

    @Override
    public void dropView() {
        avatarCallback = null;
        coverCallback = null;
        super.dropView();
    }

    public void photoClicked() {
        view.openAvatarPicker();
    }

    public void coverClicked() {
        view.openCoverPicker();
    }

    //don't use of get PREFIX
    public ImagePickCallback provideAvatarChooseCallback() {
        return avatarCallback;
    }

    public ImagePickCallback provideCoverChooseCallback() {
        return coverCallback;
    }


    public interface View extends Presenter.View {
        Bundle getArguments();

        void startLoading();

        void finishLoading();

        void openAvatarPicker();

        void openCoverPicker();

        void hideAccountContent();

        void showAccountContent();

        void showAddFriend();

        void showUpdateProfile();

        void setAvatarImage(Uri uri);

        void setCoverImage(Uri uri);

        void avatarProgressVisible(boolean visible);

        void setDateOfBirth(String format);

        void setFrom(String location);

        void setUserName(String username);

        void setUserId(String username);

        void setLivesIn(String location);

        void setTripImagesCount(int count);

        void setTripsCount(int count);

        void setBucketItemsCount(int count);

        void showFriendRequest();

        void hideFriendRequest();

        void setIsFriend(boolean isFriend);

        void setRoviaBucks(int count);

        void setDreamTripPoints(int count);

        void hideBalance();

        void showBalance();

        void setGold();

        void setPlatinum();

        void setMember();
    }
}
