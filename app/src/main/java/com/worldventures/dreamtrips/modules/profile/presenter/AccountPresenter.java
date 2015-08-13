package com.worldventures.dreamtrips.modules.profile.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.simple.BigBinaryRequest;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.BucketListModule;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedQuery;
import com.worldventures.dreamtrips.modules.feed.event.PostCreatedEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPostEventModel;
import com.worldventures.dreamtrips.modules.profile.api.GetProfileQuery;
import com.worldventures.dreamtrips.modules.profile.api.UploadAvatarCommand;
import com.worldventures.dreamtrips.modules.profile.api.UploadCoverCommand;
import com.worldventures.dreamtrips.modules.tripsimages.TripsImagesModule;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesTabsPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.util.Action;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.inject.Inject;

import icepick.Icicle;
import io.techery.scalablecropp.library.Crop;
import retrofit.mime.TypedFile;

public class AccountPresenter extends ProfilePresenter<AccountPresenter.View, User> {

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

    @Override
    protected void onProfileLoaded(User user) {
        super.onProfileLoaded(user);
        UserSession userSession = appSessionHolder.get().get();
        userSession.setUser(user);
        appSessionHolder.put(userSession);
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

    public void photoClicked() {
        view.openAvatarPicker();
    }

    public void coverClicked() {
        view.openCoverPicker();
    }

    @Override
    protected void setUserProfileInfo() {
        super.setUserProfileInfo();
        view.setTripImagesCount(user.getTripImagesCount());
        view.setBucketItemsCount(user.getBucketListItemsCount());
        view.setRoviaBucks(df.format(user.getRoviaBucks()));
        view.setDreamTripPoints(df.format(user.getDreamTripsPoints()));
    }

    public void onAvatarChosen(ChosenImage image) {
        if (image != null) {
            view.avatarProgressVisible(true);
            String fileThumbnail = image.getFileThumbnail();
            if (ValidationUtils.isUrl(fileThumbnail)) {
                cacheFacebookImage(fileThumbnail, this::uploadAvatar);
            } else {
                uploadAvatar(fileThumbnail);
            }
        }
    }

    private void uploadAvatar(String fileThumbnail) {
        final File file = new File(fileThumbnail);
        final TypedFile typedFile = new TypedFile("image/*", file);
        TrackingHelper.profileUploadStart(getAccountUserId());
        doRequest(new UploadAvatarCommand(typedFile), this::onAvatarUploadSuccess);
    }

    public void onCoverChosen(ChosenImage image) {
        if (image != null) {
            if (ValidationUtils.isUrl(image.getFileThumbnail())) {
                cacheFacebookImage(image.getFileThumbnail(), path -> Crop.prepare(path).startFrom((Fragment) view));
            } else {
                Crop.prepare(image.getFileThumbnail()).startFrom((Fragment) view);
            }
        }
    }

    //Called from onActivityResult
    public void onCoverCropped(String path, String errorMsg) {
        if (path != null) {
            this.coverTempFilePath = path;
            final File file = new File(path);
            final TypedFile typedFile = new TypedFile("image/*", file);
            view.coverProgressVisible(true);
            TrackingHelper.profileUploadStart(getAccountUserId());
            doRequest(new UploadCoverCommand(typedFile), this::onCoverUploadSuccess);

        } else {
            view.informUser(errorMsg);
        }
    }

    private void cacheFacebookImage(String url, Action<String> action) {
        String filePath = CachedEntity.getFilePath(context, CachedEntity.getFilePath(context, url));
        BigBinaryRequest bigBinaryRequest = new BigBinaryRequest(url, new File(filePath));

        dreamSpiceManager.execute(bigBinaryRequest, inputStream -> {
            action.action(filePath);
        }, null);
    }

    @Override
    protected SpiceRequest<ArrayList<BaseFeedModel>> getNextPageRequest(int page) {
        return new GetFeedQuery(page);
    }

    @Override
    protected SpiceRequest<ArrayList<BaseFeedModel>> getRefreshRequest() {
        return new GetFeedQuery(0);
    }

    public void onEvent(PostCreatedEvent event) {
        view.getAdapter().addItem(1, FeedPostEventModel.create(user, event.getTextualPost()));
        view.getAdapter().notifyItemInserted(1);
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
