package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.simple.BigBinaryRequest;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
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
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnCoverClickEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnPhotoClickEvent;
import com.worldventures.dreamtrips.modules.profile.view.fragment.AccountFragment;
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

    public static final int REQUESTER_ID = -3;

    @Inject
    RootComponentsProvider rootComponentsProvider;

    private DecimalFormat df = new DecimalFormat("#0.00");
    private String coverTempFilePath;

    @Icicle
    boolean shouldReload;
    @Icicle
    int callbackType;

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
        User currentUser = userSession.getUser();
        currentUser.setAvatar(obj.getAvatar());

        appSessionHolder.put(userSession);
        this.user.setAvatar(currentUser.getAvatar());
        this.user.setAvatarUploadInProgress(false);
        view.notifyUserChanged();
        eventBus.post(new UpdateUserInfoEvent());
    }

    private void onCoverUploadSuccess(User obj) {
        UserSession userSession = appSessionHolder.get().get();
        User currentUser = userSession.getUser();
        currentUser.setBackgroundPhotoUrl(obj.getBackgroundPhotoUrl());

        appSessionHolder.put(userSession);
        this.user.setBackgroundPhotoUrl(currentUser.getBackgroundPhotoUrl());
        this.user.setCoverUploadInProgress(false);
        view.notifyUserChanged();
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
    public void takeView(View view) {
        super.takeView(view);
        TrackingHelper.profile(getAccountUserId());
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

    private void uploadAvatar(String fileThumbnail) {
        final File file = new File(fileThumbnail);
        final TypedFile typedFile = new TypedFile("image/*", file);
        TrackingHelper.profileUploadStart(getAccountUserId());
        this.user.setAvatarUploadInProgress(true);
        view.notifyUserChanged();
        doRequest(new UploadAvatarCommand(typedFile), this::onAvatarUploadSuccess, spiceException -> {
            user.setAvatarUploadInProgress(false);
            view.notifyUserChanged();
        });
    }

    //Called from onActivityResult
    public void onCoverCropped(String path, String errorMsg) {
        if (path != null) {
            this.coverTempFilePath = path;
            final File file = new File(path);
            final TypedFile typedFile = new TypedFile("image/*", file);
            user.setCoverUploadInProgress(true);
            view.notifyUserChanged();
            TrackingHelper.profileUploadStart(getAccountUserId());
            doRequest(new UploadCoverCommand(typedFile), this::onCoverUploadSuccess, spiceException -> {
                user.setCoverUploadInProgress(false);
                view.notifyUserChanged();
            });

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
        view.getAdapter().addItem(NEW_POST_POSITION, FeedPostEventModel.create(user, event.getTextualPost()));
        view.getAdapter().notifyItemInserted(1);
    }

    ////////////////////////////////////////
    /////// Photo picking
    ////////////////////////////////////////

    public void setCallbackType(int callbackType) {
        this.callbackType = callbackType;
    }

    public void pickImage(int requestType) {
        eventBus.post(new ImagePickRequestEvent(requestType, REQUESTER_ID));
    }

    public void onEvent(ImagePickedEvent event) {
        if (event.getRequesterID() == REQUESTER_ID) {
            eventBus.cancelEventDelivery(event);
            imageSelected(event.getImages()[0]);
        }
    }

    private void imageSelected(ChosenImage chosenImage) {
        if (view != null) {
            switch (callbackType) {
                case AccountFragment.AVATAR_CALLBACK:
                    onAvatarChosen(chosenImage);
                    break;
                case AccountFragment.COVER_CALLBACK:
                    onCoverChosen(chosenImage);
                    break;
            }
        }
    }

    public void onAvatarChosen(ChosenImage image) {
        if (image != null) {
            String fileThumbnail = image.getFileThumbnail();
            if (ValidationUtils.isUrl(fileThumbnail)) {
                cacheFacebookImage(fileThumbnail, this::uploadAvatar);
            } else {
                uploadAvatar(fileThumbnail);
            }
        }
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

    public void onEvent(OnPhotoClickEvent e) {
        photoClicked();
    }

    public void onEvent(OnCoverClickEvent e) {
        coverClicked();
    }

    public interface View extends ProfilePresenter.View {
        void openAvatarPicker();

        void openCoverPicker();
    }

}
