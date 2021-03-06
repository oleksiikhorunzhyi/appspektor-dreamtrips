package com.worldventures.dreamtrips.social.ui.profile.presenter;

import android.content.Intent;

import com.raizlabs.android.dbflow.annotation.NotNull;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.Feature;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.modules.auth.api.command.LogoutCommand;
import com.worldventures.core.modules.auth.api.command.UpdateUserCommand;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.core.modules.picker.helper.PickerPermissionChecker;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.modules.video.utils.CachedModelHelper;
import com.worldventures.core.service.DownloadFileInteractor;
import com.worldventures.core.service.command.DownloadFileCommand;
import com.worldventures.core.ui.util.permission.PermissionUtils;
import com.worldventures.core.utils.ValidationUtils;
import com.worldventures.dreamtrips.modules.common.service.UserNotificationInteractor;
import com.worldventures.dreamtrips.social.service.profile.ProfileInteractor;
import com.worldventures.dreamtrips.social.service.profile.analytics.ProfileUploadingAnalyticAction;
import com.worldventures.dreamtrips.social.service.profile.analytics.ViewMyProfileAdobeAnalyticAction;
import com.worldventures.dreamtrips.social.service.profile.analytics.ViewMyProfileApptentiveAnalyticAction;
import com.worldventures.dreamtrips.social.service.profile.command.GetPrivateProfileCommand;
import com.worldventures.dreamtrips.social.service.profile.command.UploadAvatarCommand;
import com.worldventures.dreamtrips.social.service.profile.command.UploadBackgroundCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.PingAssetStatusInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CompoundOperationsCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.video.FeedItemsVideoProcessingStatusCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.social.ui.feed.presenter.UploadingListenerPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.UploadingPresenterDelegate;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetAccountTimelineCommand;
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.AccountTimelineStorageDelegate;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;
import com.worldventures.dreamtrips.social.ui.util.PermissionUIComponent;
import com.worldventures.dreamtrips.util.Action;
import com.worldventures.dreamtrips.util.SocialCropImageManager;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action0;
import timber.log.Timber;

public class AccountPresenter extends ProfilePresenter<AccountPresenter.View> implements UploadingListenerPresenter {

   private static final int DEFAULT_RATIO_X = 3;
   private static final int DEFAULT_RATIO_Y = 1;

   @Inject CompoundOperationsInteractor compoundOperationsInteractor;
   @Inject PingAssetStatusInteractor assetStatusInteractor;
   @Inject SocialCropImageManager socialCropImageManager;
   @Inject AuthInteractor authInteractor;
   @Inject ProfileInteractor profileInteractor;
   @Inject UserNotificationInteractor userNotificationInteractor;
   @Inject UploadingPresenterDelegate uploadingPresenterDelegate;
   @Inject AccountTimelineStorageDelegate accountTimelineStorageDelegate;
   @Inject CachedModelHelper cachedModelHelper;
   @Inject DownloadFileInteractor downloadFileInteractor;
   @Inject PickerPermissionChecker pickerPermissionChecker;
   @Inject PermissionUtils permissionUtils;

   @State boolean shouldReload;
   @State PickerMode pickerMode;

   List<PostCompoundOperationModel> postUploads;

   @Override
   public void onViewTaken() {
      super.onViewTaken();
      socialCropImageManager.setAspectRatio(DEFAULT_RATIO_X, DEFAULT_RATIO_Y);
      analyticsInteractor.analyticsActionPipe().send(new ViewMyProfileApptentiveAnalyticAction());
      subscribeNotificationsBadgeUpdates();
      subscribeToAvatarUpdates();
      subscribeToBackgroundUpdates();
      subscribeToStorage();
      subscribeLoadNextFeeds();
      subscribeRefreshFeeds();
      connectToCroppedImageStream();
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
      userNotificationInteractor.notificationCountChangedPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> view.updateBadgeCount(command.getFriendNotificationCount()));
   }

   void subscribeToAvatarUpdates() {
      profileInteractor.getUploadAvatarPipe()
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
      profileInteractor.getUploadBackgroundPipe()
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
      featureManager.with(Feature.SOCIAL, () -> feedInteractor.getRefreshAccountTimelinePipe()
            .send(new GetAccountTimelineCommand.Refresh()));
   }

   @Override
   public void loadNext(Date date) {
      feedInteractor.getLoadNextAccountTimelinePipe().send(new GetAccountTimelineCommand.LoadNext(date));
   }

   @Override
   protected void loadProfile() {
      view.startLoading();
      profileInteractor.getPrivateProfilePipe().createObservable(new GetPrivateProfileCommand())
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetPrivateProfileCommand>()
                  .onSuccess(command -> this.onProfileLoaded(command.getResult()))
                  .onFail(this::handleError));
   }

   void onAvatarUploadSuccess() {
      analyticsInteractor.analyticsActionPipe().send(new ProfileUploadingAnalyticAction());
      UserSession userSession = appSessionHolder.get().get();
      User currentUser = userSession.user();

      this.user.setAvatar(currentUser.getAvatar());
      this.user.setAvatarUploadInProgress(false);
      refreshFeedItems();
      view.notifyDataSetChanged();
      authInteractor.updateUserPipe().send(new UpdateUserCommand(user));
   }

   void onCoverUploadSuccess() {
      analyticsInteractor.analyticsActionPipe().send(new ProfileUploadingAnalyticAction());
      UserSession userSession = appSessionHolder.get().get();
      User currentUser = userSession.user();

      this.user.setBackgroundPhotoUrl(currentUser.getBackgroundPhotoUrl());
      this.user.setCoverUploadInProgress(false);
      refreshFeedItems();
      view.notifyDataSetChanged();
      authInteractor.updateUserPipe().send(new UpdateUserCommand(user));
   }

   public void logout() {
      authInteractor.logoutPipe().send(new LogoutCommand());
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
            }, Timber::e);
   }

   @Override
   public void openBucketList() {
      shouldReload = true;
      view.openBucketList(BucketTabsFragment.class, null);
   }

   @Override
   public void openTripImages() {
      view.openTripImages(TripImagesArgs.builder()
            .type(TripImagesArgs.TripImageType.ACCOUNT_IMAGES)
            .origin(CreateEntityBundle.Origin.PROFILE_TRIP_IMAGES)
            .userId(getAccount().getId())
            .build());
   }

   public void photoClicked() {
      askPermissions(view::openAvatarPicker);
   }

   public void coverClicked() {
      askPermissions(view::openCoverPicker);
   }

   private void askPermissions(Action0 permissionsAcceptedAction) {
      pickerPermissionChecker.registerCallback(
            permissionsAcceptedAction::call,
            () -> view.showPermissionDenied(PickerPermissionChecker.PERMISSIONS),
            () -> view.showPermissionExplanationText(PickerPermissionChecker.PERMISSIONS));

      pickerPermissionChecker.checkPermission();
   }

   public void recheckPermission(String[] permissions, boolean userAnswer) {
      if (permissionUtils.equals(permissions, PickerPermissionChecker.PERMISSIONS)) {
         pickerPermissionChecker.recheckPermission(userAnswer);
      }
   }

   private void uploadAvatar(String fileThumbnail) {
      analyticsInteractor.analyticsActionPipe().send(new ProfileUploadingAnalyticAction());
      profileInteractor.getUploadAvatarPipe().send(new UploadAvatarCommand(fileThumbnail));
      user.setAvatarUploadInProgress(true);
      refreshFeedItems();
      view.notifyDataSetChanged();
   }

   private void onCoverCropped(File croppedFile, String errorMsg) {
      if (croppedFile != null) {
         analyticsInteractor.analyticsActionPipe().send(new ProfileUploadingAnalyticAction());
         profileInteractor.getUploadBackgroundPipe().send(new UploadBackgroundCommand(croppedFile.getPath()));
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
      pickerMode = PickerMode.AVATAR;
      view.showMediaPicker();
   }

   public void onCoverClicked() {
      pickerMode = PickerMode.COVER;
      view.showMediaPicker();
   }

   public void imageSelected(MediaPickerAttachment mediaAttachment) {
      if (mediaAttachment.getChosenImages().isEmpty()) {
         return;
      }

      PhotoPickerModel image = mediaAttachment.getChosenImages().get(0);
      if (image != null) {
         switch (pickerMode) {
            case AVATAR:
               onAvatarChosen(image);
               break;
            case COVER:
               onCoverChosen(image);
               break;
            default:
               break;
         }
      }
   }

   private void onAvatarChosen(@NotNull PhotoPickerModel image) {
      String imageAbsolutePath = image.getAbsolutePath();
      if (ValidationUtils.isUrl(imageAbsolutePath)) {
         cacheFacebookImage(imageAbsolutePath, this::uploadAvatar);
      } else {
         uploadAvatar(imageAbsolutePath);
      }
   }

   private void onCoverChosen(@NotNull PhotoPickerModel image) {
      String imageAbsolutePath = image.getAbsolutePath();
      if (ValidationUtils.isUrl(imageAbsolutePath)) {
         cacheFacebookImage(imageAbsolutePath, realPath -> view.cropImage(socialCropImageManager, realPath));
      } else {
         view.cropImage(socialCropImageManager, imageAbsolutePath);
      }
   }

   public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
      return socialCropImageManager.onActivityResult(requestCode, resultCode, data);
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

   public interface View extends ProfilePresenter.View, PermissionUIComponent {

      void openAvatarPicker();

      void openCoverPicker();

      void updateBadgeCount(int count);

      void showMediaPicker();

      void cropImage(SocialCropImageManager socialCropImageManager, String path);

      void refreshFeedItems(List<FeedItem> items, UploadingPostsList uploadingPostsList, User user);
   }

   public enum PickerMode {
      AVATAR, COVER
   }
}
