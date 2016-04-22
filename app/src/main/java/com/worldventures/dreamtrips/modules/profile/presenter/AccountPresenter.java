package com.worldventures.dreamtrips.modules.profile.presenter;

import com.messenger.delegate.CropImageDelegate;
import com.octo.android.robospice.request.simple.BigBinaryRequest;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.LogoutDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.feed.api.GetUserTimelineQuery;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;
import com.worldventures.dreamtrips.modules.profile.api.GetProfileQuery;
import com.worldventures.dreamtrips.modules.profile.api.UploadAvatarCommand;
import com.worldventures.dreamtrips.modules.profile.api.UploadCoverCommand;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnCoverClickEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnPhotoClickEvent;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.util.Action;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import icepick.State;
import retrofit.mime.TypedFile;
import rx.Subscription;
import timber.log.Timber;

public class AccountPresenter extends ProfilePresenter<AccountPresenter.View, User> {

    public static final int AVATAR_MEDIA_REQUEST_ID = 155322;
    public static final int COVER_MEDIA_REQUEST_ID = 155323;

    public static final int DEFAULT_RATIO_X = 3;
    public static final int DEFAULT_RATIO_Y = 1;

    @Inject
    RootComponentsProvider rootComponentsProvider;
    @Inject
    LogoutDelegate logoutDelegate;
    @Inject
    MediaPickerManager mediaPickerManager;
    @Inject
    CropImageDelegate cropImageDelegate;

    private Subscription mediaSubscription;

    private String coverTempFilePath;

    @State
    boolean shouldReload;
    @State
    int mediaRequestId;

    public AccountPresenter() {
        super();
    }

    @Override
    protected void loadProfile() {
        view.startLoading();
        doRequest(new GetProfileQuery(appSessionHolder), this::onProfileLoaded);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldReload) {
            shouldReload = false;
            loadProfile();
        }
        //
        logoutDelegate.setDreamSpiceManager(dreamSpiceManager);
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
        eventBus.postSticky(new UpdateUserInfoEvent(user));
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
        eventBus.postSticky(new UpdateUserInfoEvent(user));
    }

    @Override
    protected void onProfileLoaded(User user) {
        super.onProfileLoaded(user);
    }

    public void logout() {
        logoutDelegate.logout();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        TrackingHelper.profile(getAccountUserId());
        //
        mediaSubscription = mediaPickerManager.toObservable()
                .filter(attachment -> (attachment.requestId == AVATAR_MEDIA_REQUEST_ID
                        || attachment.requestId == COVER_MEDIA_REQUEST_ID) && attachment.chosenImages.size() > 0)
                .subscribe(mediaAttachment -> {
                    if (view != null) {
                        view.hideMediaPicker();
                        //
                        imageSelected(mediaAttachment);
                    }
                }, error -> {
                    Timber.e(error, "");
                });

        cropImageDelegate.setAspectRatio(DEFAULT_RATIO_X, DEFAULT_RATIO_Y);
        connectToCroppedImageStream();
    }

    private void connectToCroppedImageStream() {
        view.bind(cropImageDelegate.getCroppedImagesStream()
                .compose(new IoToMainComposer<>()))
                .subscribe(fileNotification -> {
                    if (fileNotification.isOnError()) {
                        onCoverCropped(null, fileNotification.getThrowable().toString());
                    } else {
                        onCoverCropped(fileNotification.getValue(), null);
                    }
                }, error -> {
                    Timber.e(error, "");
                });
    }

    @Override
    public void dropView() {
        super.dropView();
        if (!mediaSubscription.isUnsubscribed()) mediaSubscription.unsubscribe();
    }

    @Override
    public void openBucketList() {
        shouldReload = true;
        view.openBucketList(Route.BUCKET_TABS, null);
    }

    @Override
    public void openTripImages() {
        view.openTripImages(Route.ACCOUNT_IMAGES,
                new TripsImagesBundle(TripImagesType.ACCOUNT_IMAGES, getAccount().getId()));
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
            handleError(spiceException);
            user.setAvatarUploadInProgress(false);
            view.notifyUserChanged();
        });
    }

    public void onCoverCropped(File croppedFile, String errorMsg) {
        if (croppedFile != null) {
            this.coverTempFilePath = croppedFile.getPath();
            final TypedFile typedFile = new TypedFile("image/*", croppedFile);
            user.setCoverUploadInProgress(true);
            view.notifyUserChanged();
            TrackingHelper.profileUploadStart(getAccountUserId());
            doRequest(new UploadCoverCommand(typedFile), this::onCoverUploadSuccess, spiceException -> {
                handleError(spiceException);
                user.setCoverUploadInProgress(false);
                view.notifyUserChanged();
            });

        } else {
            view.informUser(errorMsg);
        }
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedItem>> getRefreshFeedRequest(Date date) {
        return new GetUserTimelineQuery(user.getId());
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedItem>> getNextPageFeedRequest(Date date) {
        return new GetUserTimelineQuery(user.getId(), date);
    }

    ////////////////////////////////////////
    /////// Photo picking
    ////////////////////////////////////////

    public void onAvatarClicked() {
        this.mediaRequestId = AVATAR_MEDIA_REQUEST_ID;
        view.showMediaPicker(mediaRequestId);
    }

    public void onCoverClicked() {
        this.mediaRequestId = COVER_MEDIA_REQUEST_ID;
        view.showMediaPicker(mediaRequestId);
    }

    public void onEventMainThread(HeaderCountChangedEvent event) {
        view.updateBadgeCount(snappyRepository.getFriendsRequestsCount());
    }

    private void imageSelected(MediaAttachment mediaAttachment) {
        PhotoGalleryModel image = mediaAttachment.chosenImages.get(0);
        switch (mediaAttachment.requestId) {
            case AVATAR_MEDIA_REQUEST_ID:
                onAvatarChosen(image);
                break;
            case COVER_MEDIA_REQUEST_ID:
                onCoverChosen(image);
                break;
        }
    }

    public void onAvatarChosen(PhotoGalleryModel image) {
        if (image != null) {
            String filePath = image.getOriginalPath();
            if (ValidationUtils.isUrl(filePath)) {
                cacheFacebookImage(filePath, this::uploadAvatar);
            } else {
                uploadAvatar(filePath);
            }
        }
    }

    private void cacheFacebookImage(String url, Action<String> action) {
        String filePath = CachedEntity.getFilePath(context, CachedEntity.getFilePath(context, url));
        BigBinaryRequest bigBinaryRequest = new BigBinaryRequest(url, new File(filePath));

        doRequest(bigBinaryRequest, inputStream -> action.action(filePath));
    }

    public void onCoverChosen(PhotoGalleryModel image) {
        cropImageDelegate.cropImage(image.getOriginalPath());
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

        void updateBadgeCount(int count);

        void inject(Object object);

        void showMediaPicker(int requestId);

        void hideMediaPicker();
    }

}
