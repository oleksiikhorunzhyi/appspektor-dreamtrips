package com.worldventures.dreamtrips.modules.profile.presenter;

import android.content.Intent;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.auth.api.command.LogoutCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UpdateUserCommand;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.common.command.DownloadFileCommand;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.SocialCropImageManager;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.service.LogoutInteractor;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.modules.feed.presenter.UploadingListenerPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.UploadingPresenterDelegate;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountTimelineCommand;
import com.worldventures.dreamtrips.modules.profile.service.ProfileInteractor;
import com.worldventures.dreamtrips.modules.profile.service.command.GetPrivateProfileCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.UploadAvatarCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.UploadBackgroundCommand;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
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

   @Inject RootComponentsProvider rootComponentsProvider;
   @Inject LogoutInteractor logoutInteractor;
   @Inject DownloadFileInteractor downloadFileInteractor;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;
   @Inject SocialCropImageManager socialCropImageManager;
   @Inject AuthInteractor authInteractor;
   @Inject ProfileInteractor profileInteractor;
   @Inject NotificationCountEventDelegate notificationCountEventDelegate;
   @Inject SnappyRepository db;
   @Inject UploadingPresenterDelegate uploadingPresenterDelegate;

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
                     view.notifyUserChanged();
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
                     view.notifyUserChanged();
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

   private void subscribeLoadNextFeeds() {
      feedInteractor.getLoadNextAccountTimelinePipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetAccountTimelineCommand.LoadNext>()
                  .onFail(this::loadMoreItemsError)
                  .onSuccess(action -> addFeedItems(action.getResult())));
   }

   private void subscribeToBackgroundUploadingOperations() {
      backgroundUploadingInteractor.compoundOperationsPipe()
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<CompoundOperationsCommand>()
                  .onSuccess(compoundOperationsCommand -> {
                     postUploads = Queryable.from(compoundOperationsCommand.getResult())
                           .cast(PostCompoundOperationModel.class).toList();
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
      view.notifyUserChanged();
      authInteractor.updateUserPipe().send(new UpdateUserCommand(user));
   }

   private void onCoverUploadSuccess() {
      TrackingHelper.profileUploadFinish(getAccountUserId());
      UserSession userSession = appSessionHolder.get().get();
      User currentUser = userSession.getUser();

      this.user.setBackgroundPhotoUrl(currentUser.getBackgroundPhotoUrl());
      this.user.setCoverUploadInProgress(false);
      view.notifyUserChanged();
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
      view.notifyUserChanged();
   }

   private void onCoverCropped(File croppedFile, String errorMsg) {
      if (croppedFile != null) {
         TrackingHelper.profileUploadStart(getAccountUserId());
         profileInteractor.uploadBackgroundPipe().send(new UploadBackgroundCommand(croppedFile.getPath()));
         user.setCoverUploadInProgress(true);
         view.notifyUserChanged();
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

   private void onAvatarChosen(PhotoGalleryModel image) {
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
      String filePath = CachedEntity.getFilePath(context, url);
      downloadFileInteractor.getDownloadFileCommandPipe()
            .createObservable(new DownloadFileCommand(new File(filePath), url))
            .subscribe(new ActionStateSubscriber<DownloadFileCommand>()
                  .onSuccess(downloadFileCommand -> action.action(filePath))
                  .onFail(this::handleError));
   }

   private void onCoverChosen(PhotoGalleryModel image) {
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
      view.refreshFeedItems(feedItems, new UploadingPostsList(postUploads));
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

      void refreshFeedItems(List<FeedItem> items, UploadingPostsList uploadingPostsList);
   }
}
