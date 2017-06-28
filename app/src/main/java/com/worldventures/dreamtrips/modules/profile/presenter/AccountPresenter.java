package com.worldventures.dreamtrips.modules.profile.presenter;

import android.content.Intent;

import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.auth.api.command.LogoutCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UpdateUserCommand;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.FeedItemsVideoProcessingStatusInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.FeedItemsVideoProcessingStatusCommand;
import com.worldventures.dreamtrips.modules.common.command.DownloadFileCommand;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.SocialCropImageManager;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.service.LogoutInteractor;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.modules.feed.presenter.UploadingListenerPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.UploadingPresenterDelegate;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountTimelineCommand;
import com.worldventures.dreamtrips.modules.feed.storage.command.AccountTimelineStorageCommand;
import com.worldventures.dreamtrips.modules.feed.storage.delegate.AccountTimelineStorageDelegate;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.profile.service.ProfileInteractor;
import com.worldventures.dreamtrips.modules.profile.service.command.GetPrivateProfileCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.UploadAvatarCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.UploadBackgroundCommand;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;
import com.worldventures.dreamtrips.util.Action;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class AccountPresenter extends ProfilePresenter<AccountPresenter.View, User>
   implements UploadingListenerPresenter {

   private static final int AVATAR_MEDIA_REQUEST_ID = 155322;
   private static final int COVER_MEDIA_REQUEST_ID = 155323;
   private static final int DEFAULT_RATIO_X = 3;
   private static final int DEFAULT_RATIO_Y = 1;

   @Inject LogoutInteractor logoutInteractor;
   @Inject DownloadFileInteractor downloadFileInteractor;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;
   @Inject FeedItemsVideoProcessingStatusInteractor feedItemsVideoProcessingStatusInteractor;
   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;
   @Inject SocialCropImageManager socialCropImageManager;
   @Inject AuthInteractor authInteractor;
   @Inject ProfileInteractor profileInteractor;
   @Inject NotificationCountEventDelegate notificationCountEventDelegate;
   @Inject SnappyRepository db;
   @Inject UploadingPresenterDelegate uploadingPresenterDelegate;
   @Inject AccountTimelineStorageDelegate accountTimelineStorageDelegate;
   @Inject CachedModelHelper cachedModelHelper;

   @State boolean shouldReload;
   @State int mediaRequestId;

   private List<PostCompoundOperationModel> postUploads;

   public AccountPresenter() {
      super();
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      socialCropImageManager.setAspectRatio(DEFAULT_RATIO_X, DEFAULT_RATIO_Y);
      TrackingHelper.profile(getAccountUserId());
      subscribeNotificationsBadgeUpdates();
      subscribeToAvatarUpdates();
      subscribeToBackgroundUpdates();
      subscribeToStorage();
      subscribeLoadNextFeeds();
      subscribeRefreshFeeds();
      connectToCroppedImageStream();
      subscribeToMediaPicker();
      subscribeToBackgroundUploadingOperations();
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

   private void subscribeNotificationsBadgeUpdates() {
      notificationCountEventDelegate.getObservable()
            .compose(bindViewToMainComposer())
            .subscribe(o -> view.updateBadgeCount(db.getFriendsRequestsCount()));
   }

   private void subscribeToAvatarUpdates() {
      profileInteractor.uploadAvatarPipe()
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<UploadAvatarCommand>()
                  .onSuccess(command -> onAvatarUploadSuccess())
                  .onFail((command, e) -> {
                     handleError(command, e);
                     user.setAvatarUploadInProgress(false);
                     refreshFeedItems();
                  })
            );
   }

   private void subscribeToBackgroundUpdates() {
      profileInteractor.uploadBackgroundPipe()
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<UploadBackgroundCommand>()
                  .onSuccess(command -> onCoverUploadSuccess())
                  .onFail((command, e) -> {
                     handleError(command, e);
                     user.setCoverUploadInProgress(false);
                     refreshFeedItems();
                  })
            );
   }

   private void subscribeToMediaPicker() {
      mediaPickerEventDelegate.getObservable()
            .filter(attachment -> attachment.chosenImages.size() > 0)
            .compose(bindView())
            .subscribe(mediaAttachment -> {
               view.hideMediaPicker();
               imageSelected(mediaAttachment);
            }, error -> Timber.e(error, ""));
   }

   private void subscribeRefreshFeeds() {
      feedInteractor.getRefreshAccountTimelinePipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetAccountTimelineCommand.Refresh>()
                  .onFail(this::refreshFeedError)
                  .onSuccess(action -> refreshFeedSucceed(action.getResult())));
   }

   private void subscribeToStorage() {
      accountTimelineStorageDelegate.startUpdatingStorage()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<AccountTimelineStorageCommand>()
                  .onSuccess(storageCommand -> {
                     List<FeedItem> items = storageCommand.getResult();
                     feedItemsVideoProcessingStatusInteractor.videosProcessingPipe()
                           .send(new FeedItemsVideoProcessingStatusCommand(items));
                     onItemsChanged(items);
                  })
                  .onFail(this::handleError));
   }

   private void subscribeLoadNextFeeds() {
      feedInteractor.getLoadNextAccountTimelinePipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetAccountTimelineCommand.LoadNext>()
                  .onFail(this::loadMoreItemsError)
                  .onSuccess(action -> addFeedItems(action.getResult())));
   }

   private void subscribeToBackgroundUploadingOperations() {
      compoundOperationsInteractor.compoundOperationsPipe()
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<CompoundOperationsCommand>()
                  .onSuccess(compoundOperationsCommand -> {
                     postUploads = compoundOperationsCommand.getResult();
                     refreshFeedItems();
                  }));
   }

   @Override
   public void refreshFeed() {
      feedInteractor.getRefreshAccountTimelinePipe().send(new GetAccountTimelineCommand.Refresh());
   }

   @Override
   public void loadNext(Date date) {
      feedInteractor.getLoadNextAccountTimelinePipe().send(new GetAccountTimelineCommand.LoadNext(date));
   }

   @Override
   protected void loadProfile() {
      view.startLoading();
      profileInteractor.privateProfilePipe().createObservable(new GetPrivateProfileCommand())
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetPrivateProfileCommand>()
                  .onSuccess(command -> this.onProfileLoaded(command.getResult()))
                  .onFail(this::handleError));
   }

   private void onAvatarUploadSuccess() {
      TrackingHelper.profileUploadFinish(getAccountUserId());
      UserSession userSession = appSessionHolder.get().get();
      User currentUser = userSession.getUser();

      this.user.setAvatar(currentUser.getAvatar());
      this.user.setAvatarUploadInProgress(false);
      refreshFeedItems();
      authInteractor.updateUserPipe().send(new UpdateUserCommand(user));
   }

   private void onCoverUploadSuccess() {
      TrackingHelper.profileUploadFinish(getAccountUserId());
      UserSession userSession = appSessionHolder.get().get();
      User currentUser = userSession.getUser();

      this.user.setBackgroundPhotoUrl(currentUser.getBackgroundPhotoUrl());
      this.user.setCoverUploadInProgress(false);
      refreshFeedItems();
      authInteractor.updateUserPipe().send(new UpdateUserCommand(user));
   }

   public void logout() {
      logoutInteractor.logoutPipe().send(new LogoutCommand());
   }

   private void connectToCroppedImageStream() {
      socialCropImageManager.getCroppedImagesStream()
            .compose(bindViewToMainComposer())
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
   public void openBucketList() {
      shouldReload = true;
      view.openBucketList(Route.BUCKET_TABS, null);
   }

   @Override
   public void openTripImages() {
      view.openTripImages(Route.ACCOUNT_IMAGES, new TripsImagesBundle(TripImagesType.ACCOUNT_IMAGES_FROM_PROFILE, getAccount()
            .getId()));
   }

   public void photoClicked() {
      view.openAvatarPicker();
   }

   public void coverClicked() {
      view.openCoverPicker();
   }

   private void uploadAvatar(String fileThumbnail) {
      TrackingHelper.profileUploadStart(getAccountUserId());
      profileInteractor.uploadAvatarPipe().send(new UploadAvatarCommand(fileThumbnail));
      user.setAvatarUploadInProgress(true);
      refreshFeedItems();
   }

   private void onCoverCropped(File croppedFile, String errorMsg) {
      if (croppedFile != null) {
         TrackingHelper.profileUploadStart(getAccountUserId());
         profileInteractor.uploadBackgroundPipe().send(new UploadBackgroundCommand(croppedFile.getPath()));
         user.setCoverUploadInProgress(true);
         refreshFeedItems();
      } else {
         view.informUser(errorMsg);
      }
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

   private void imageSelected(MediaAttachment mediaAttachment) {
      PhotoPickerModel image = mediaAttachment.chosenImages.get(0);
      switch (mediaAttachment.requestId) {
         case AVATAR_MEDIA_REQUEST_ID:
            onAvatarChosen(image);
            break;
         case COVER_MEDIA_REQUEST_ID:
            onCoverChosen(image);
            break;
      }
   }

   private void onAvatarChosen(PhotoPickerModel image) {
      if (image != null) {
         String filePath = image.getAbsolutePath();
         if (ValidationUtils.isUrl(filePath)) {
            cacheFacebookImage(filePath, this::uploadAvatar);
         } else {
            uploadAvatar(filePath);
         }
      }
   }

   private void cacheFacebookImage(String url, Action<String> action) {
      String filePath = cachedModelHelper.getFilePath(url);
      downloadFileInteractor.getDownloadFileCommandPipe()
            .createObservable(new DownloadFileCommand(new File(filePath), url))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<DownloadFileCommand>()
                  .onSuccess(downloadFileCommand -> action.action(filePath))
                  .onFail(this::handleError));
   }

   private void onCoverChosen(PhotoPickerModel image) {
      if (image != null) {
         String filePath = image.getAbsolutePath();
         if (ValidationUtils.isUrl(filePath)) {
            cacheFacebookImage(filePath, this::cropImage);
         } else {
            cropImage(filePath);
         }
      }
   }

   private void cropImage(String filePath) {
      view.cropImage(socialCropImageManager, filePath);
   }

   public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
      return socialCropImageManager.onActivityResult(requestCode, resultCode, data);
   }

   @Override
   public void refreshFeedItems() {
      view.refreshFeedItems(feedItems, new UploadingPostsList(postUploads), user);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Uploading handling
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void onUploadResume(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadResume(compoundOperationModel);
   }

   @Override
   public void onUploadPaused(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadPaused(compoundOperationModel);
   }

   @Override
   public void onUploadRetry(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadRetry(compoundOperationModel);
   }

   @Override
   public void onUploadCancel(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadCancel(compoundOperationModel);
   }

   public interface View extends ProfilePresenter.View {

      void openAvatarPicker();

      void openCoverPicker();

      void updateBadgeCount(int count);

      void showMediaPicker(int requestId);

      void hideMediaPicker();

      void cropImage(SocialCropImageManager socialCropImageManager, String path);

      void refreshFeedItems(List<FeedItem> items, UploadingPostsList uploadingPostsList, User user);
   }
}
