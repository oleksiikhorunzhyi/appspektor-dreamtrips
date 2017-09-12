package com.worldventures.dreamtrips.modules.profile.presenter;

import android.content.Intent;

import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.auth.api.command.LogoutCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UpdateUserCommand;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.PingAssetStatusInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.FeedItemsVideoProcessingStatusCommand;
import com.worldventures.dreamtrips.modules.common.command.DownloadFileCommand;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.SocialCropImageManager;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.service.LogoutInteractor;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.modules.feed.presenter.UploadingListenerPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.UploadingPresenterDelegate;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountTimelineCommand;
import com.worldventures.dreamtrips.modules.feed.storage.delegate.AccountTimelineStorageDelegate;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.profile.service.ProfileInteractor;
import com.worldventures.dreamtrips.modules.profile.service.analytics.ProfileUploadingAnalyticAction;
import com.worldventures.dreamtrips.modules.profile.service.analytics.ViewMyProfileAdobeAnalyticAction;
import com.worldventures.dreamtrips.modules.profile.service.analytics.ViewMyProfileApptentiveAnalyticAction;
import com.worldventures.dreamtrips.modules.profile.service.command.GetPrivateProfileCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.UploadAvatarCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.UploadBackgroundCommand;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesArgs;
import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;
import com.worldventures.dreamtrips.util.Action;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class AccountPresenter extends ProfilePresenter<AccountPresenter.View>
      implements UploadingListenerPresenter {

   private static final int AVATAR_MEDIA_REQUEST_ID = 155322;
   private static final int COVER_MEDIA_REQUEST_ID = 155323;
   private static final int DEFAULT_RATIO_X = 3;
   private static final int DEFAULT_RATIO_Y = 1;

   @Inject LogoutInteractor logoutInteractor;
   @Inject DownloadFileInteractor downloadFileInteractor;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;
   @Inject PingAssetStatusInteractor assetStatusInteractor;
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

   List<PostCompoundOperationModel> postUploads;

   public AccountPresenter() {
      super();
   }

   @Override
   public void onViewTaken() {
      socialCropImageManager.setAspectRatio(DEFAULT_RATIO_X, DEFAULT_RATIO_Y);
      analyticsInteractor.analyticsActionPipe().send(new ViewMyProfileApptentiveAnalyticAction());
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
      analyticsInteractor.analyticsActionPipe().send(new ViewMyProfileAdobeAnalyticAction());
   }

   @Override
   public void onInjected() {
      super.onInjected();
      user = getAccount();
   }

   void subscribeNotificationsBadgeUpdates() {
      notificationCountEventDelegate.getObservable()
            .compose(bindViewToMainComposer())
            .subscribe(o -> view.updateBadgeCount(db.getFriendsRequestsCount()));
   }

   void subscribeToAvatarUpdates() {
      profileInteractor.uploadAvatarPipe()
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<UploadAvatarCommand>()
                  .onSuccess(command -> onAvatarUploadSuccess())
                  .onFail((command, e) -> {
                     handleError(command, e);
                     user.setAvatarUploadInProgress(false);
                     refreshFeedItems();
                     view.notifyDataSetChanged();
                  })
            );
   }

   void subscribeToBackgroundUpdates() {
      profileInteractor.uploadBackgroundPipe()
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<UploadBackgroundCommand>()
                  .onSuccess(command -> onCoverUploadSuccess())
                  .onFail((command, e) -> {
                     handleError(command, e);
                     user.setCoverUploadInProgress(false);
                     refreshFeedItems();
                     view.notifyDataSetChanged();
                  })
            );
   }

   void subscribeToMediaPicker() {
      mediaPickerEventDelegate.getObservable()
            .compose(bindViewToMainComposer())
            .subscribe(mediaAttachment -> {
               view.hideMediaPicker();
               imageSelected(mediaAttachment);
            }, error -> Timber.e(error, ""));
   }

   void subscribeRefreshFeeds() {
      feedInteractor.getRefreshAccountTimelinePipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetAccountTimelineCommand.Refresh>()
                  .onSuccess(action -> refreshFeedSucceed(action.getResult()))
                  .onFail(this::refreshFeedError));
   }

   void subscribeToStorage() {
      accountTimelineStorageDelegate.observeStorageCommand()
            .compose(bindViewToMainComposer())
            .map(Command::getResult)
            .subscribe(this::timeLineUpdated, this::handleError);
   }

   void timeLineUpdated(List<FeedItem> items) {
      assetStatusInteractor.feedItemsVideoProcessingPipe().send(new FeedItemsVideoProcessingStatusCommand(items));
      onItemsChanged(items);
   }

   void subscribeLoadNextFeeds() {
      feedInteractor.getLoadNextAccountTimelinePipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetAccountTimelineCommand.LoadNext>()
                  .onSuccess(action -> addFeedItems(action.getResult()))
                  .onFail(this::loadMoreItemsError));
   }

   void subscribeToBackgroundUploadingOperations() {
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

   void onAvatarUploadSuccess() {
      analyticsInteractor.analyticsActionPipe().send(new ProfileUploadingAnalyticAction());
      UserSession userSession = appSessionHolder.get().get();
      User currentUser = userSession.getUser();

      this.user.setAvatar(currentUser.getAvatar());
      this.user.setAvatarUploadInProgress(false);
      refreshFeedItems();
      view.notifyDataSetChanged();
      authInteractor.updateUserPipe().send(new UpdateUserCommand(user));
   }

   void onCoverUploadSuccess() {
      analyticsInteractor.analyticsActionPipe().send(new ProfileUploadingAnalyticAction());
      UserSession userSession = appSessionHolder.get().get();
      User currentUser = userSession.getUser();

      this.user.setBackgroundPhotoUrl(currentUser.getBackgroundPhotoUrl());
      this.user.setCoverUploadInProgress(false);
      refreshFeedItems();
      view.notifyDataSetChanged();
      authInteractor.updateUserPipe().send(new UpdateUserCommand(user));
   }

   public void logout() {
      logoutInteractor.logoutPipe().send(new LogoutCommand());
   }

   void connectToCroppedImageStream() {
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
      view.openTripImages(Route.ACCOUNT_IMAGES, TripImagesArgs.builder()
            .route(Route.ACCOUNT_IMAGES)
            .origin(CreateEntityBundle.Origin.PROFILE_TRIP_IMAGES)
            .userId(getAccount().getId())
            .build());
   }

   public void photoClicked() {
      view.openAvatarPicker();
   }

   public void coverClicked() {
      view.openCoverPicker();
   }

   private void uploadAvatar(String fileThumbnail) {
      analyticsInteractor.analyticsActionPipe().send(new ProfileUploadingAnalyticAction());
      profileInteractor.uploadAvatarPipe().send(new UploadAvatarCommand(fileThumbnail));
      user.setAvatarUploadInProgress(true);
      refreshFeedItems();
      view.notifyDataSetChanged();
   }

   private void onCoverCropped(File croppedFile, String errorMsg) {
      if (croppedFile != null) {
         analyticsInteractor.analyticsActionPipe().send(new ProfileUploadingAnalyticAction());
         profileInteractor.uploadBackgroundPipe().send(new UploadBackgroundCommand(croppedFile.getPath()));
         user.setCoverUploadInProgress(true);
         refreshFeedItems();
         view.notifyDataSetChanged();
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
      PhotoPickerModel image = mediaAttachment.chosenImage;
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
