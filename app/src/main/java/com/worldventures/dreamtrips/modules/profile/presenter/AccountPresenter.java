package com.worldventures.dreamtrips.modules.profile.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.BucketListModule;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.profile.api.GetProfileQuery;
import com.worldventures.dreamtrips.modules.profile.api.UploadAvatarCommand;
import com.worldventures.dreamtrips.modules.profile.api.UploadCoverCommand;
import com.worldventures.dreamtrips.modules.tripsimages.TripsImagesModule;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesTabsPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.io.File;
import java.text.DecimalFormat;

import javax.inject.Inject;

import icepick.Icicle;
import io.techery.scalablecropp.library.Crop;
import retrofit.mime.TypedFile;

public class AccountPresenter extends ProfilePresenter<AccountPresenter.View> {

    @Inject
    RootComponentsProvider rootComponentsProvider;

    private DecimalFormat df = new DecimalFormat("#0.00");
    private String coverTempFilePath;

    @Icicle
    boolean shouldReload;

    public AccountPresenter() {
        super();
    }

    @Override
    protected void loadProfile() {
        view.startLoading();
        doRequest(new GetProfileQuery(), this::onProfileLoaded);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldReload) {
            shouldReload = false;
            loadProfile();
        }
    }

    @Override
    public void onInjected() {
        super.onInjected();
        user = getAccount();
    }

    private void onAvatarUploadSuccess(User obj) {
        TrackingHelper.profileUploadFinish(getAccountUserId());
        UserSession userSession = appSessionHolder.get().get();
        User user = userSession.getUser();
        user.setAvatar(obj.getAvatar());

        appSessionHolder.put(userSession);
        view.setAvatarImage(Uri.parse(user.getAvatar().getMedium()));
        view.avatarProgressVisible(false);
        eventBus.post(new UpdateUserInfoEvent());
    }

    private void onCoverUploadSuccess(User obj) {
        UserSession userSession = appSessionHolder.get().get();
        User user = userSession.getUser();
        user.setBackgroundPhotoUrl(obj.getBackgroundPhotoUrl());
        this.user = user;
        appSessionHolder.put(userSession);
        view.setCoverImage(Uri.parse(user.getBackgroundPhotoUrl()));
        view.coverProgressVisible(false);
        if (coverTempFilePath != null) {
            new File(coverTempFilePath).delete();
        }
        eventBus.post(new UpdateUserInfoEvent());
    }

    public void logout() {
        this.appSessionHolder.destroy();
        snappyRepository.clearAll();
        activityRouter.finish();
        activityRouter.openLogin();
    }

    @Override
    public void handleError(SpiceException error) {
        view.avatarProgressVisible(false);
        super.handleError(error);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        TrackingHelper.profile(getAccountUserId());
        view.setSocial(featureManager.available(Feature.SOCIAL));
        view.setSocial(true);
    }

    @Override
    public void openBucketList() {
        shouldReload = true;
        activityRouter.openComponentActivity(rootComponentsProvider
                .getComponentByKey(BucketListModule.BUCKETLIST));
    }

    @Override
    public void openTripImages() {
        shouldReload = true;
        Bundle args = new Bundle();
        args.putInt(TripImagesTabsPresenter.SELECTION_EXTRA, TripImagesListFragment.Type.MY_IMAGES.ordinal());
        activityRouter.openComponentActivity(rootComponentsProvider
                .getComponentByKey(TripsImagesModule.TRIP_IMAGES), args);
    }

    @Override
    public void dropView() {
        super.dropView();
    }

    public void photoClicked() {
        view.openAvatarPicker();
    }

    public void coverClicked() {
        view.openCoverPicker();
    }

    @Override
    protected void setUserProfileInfo() {
        super.setUserProfileInfo();
        view.setRoviaBucks(df.format(user.getRoviaBucks()));
        view.setDreamTripPoints(df.format(user.getDreamTripsPoints()));
    }

    public void onAvatarChosen(Fragment fragment, ChosenImage image, String error) {
        if (image != null) {
            final File file = new File(image.getFileThumbnail());
            final TypedFile typedFile = new TypedFile("image/*", file);
            view.avatarProgressVisible(true);
            TrackingHelper.profileUploadStart(getAccountUserId());
            doRequest(new UploadAvatarCommand(typedFile), this::onAvatarUploadSuccess);
        }
    }

    public void onCoverChosen(Fragment fragment, ChosenImage image, String error) {
        if (image != null) {
            Crop.prepare(image.getFileThumbnail()).ratio(3, 2).startFrom((Fragment) view);
        }
    }

    //Called from onActivityResult
    public void onCoverCropped(String path, String errorMsg) {
        if (path != null) {
            this.coverTempFilePath = path;
            view.setCoverImage(Uri.fromFile(new File(path)));
            final File file = new File(path);
            final TypedFile typedFile = new TypedFile("image/*", file);
            view.coverProgressVisible(true);
            TrackingHelper.profileUploadStart(getAccountUserId());
            doRequest(new UploadCoverCommand(typedFile), this::onCoverUploadSuccess);

        } else {
            view.informUser(errorMsg);
        }
    }

    public interface View extends ProfilePresenter.View {
        void avatarProgressVisible(boolean visible);

        void coverProgressVisible(boolean visible);

        void openAvatarPicker();

        void openCoverPicker();

        void setRoviaBucks(String count);

        void setDreamTripPoints(String count);

    }

}
