package com.worldventures.dreamtrips.modules.profile.presenter;

import android.net.Uri;
import android.os.Bundle;
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
        TrackingHelper.profile(getUserId());
        User user = this.appSessionHolder.get().get().getUser();
        view.setUserName(user.getUsername());
        view.setDateOfBirth(DateTimeUtils.convertDateToString(user.getBirthDate(),
                DateFormat.getMediumDateFormat(context)));
        view.setUserId(user.getUsername());
        view.setLivesIn(user.getLocation());
        view.setFrom(user.getLocation());

        view.setAvatarImage(Uri.parse(user.getAvatar().getMedium()));
        view.setCoverImage(Uri.fromFile(new File(user.getCoverPath())));

        //TODO replace with real values
        view.setTripImagesCount(0);
        view.setTripsCount(0);
        view.setBucketItemsCount(0);
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

    //don't use of get PREFIX
    public ImagePickCallback provideAvatarChooseCallback() {
        return avatarCallback;
    }

    public ImagePickCallback provideCoverChooseCallback() {
        return coverCallback;
    }


    public interface View extends Presenter.View {
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
    }
}
