package com.worldventures.dreamtrips.modules.profile.presenter;

import android.content.Intent;

import com.octo.android.robospice.request.simple.BigBinaryRequest;
import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.auth.api.command.LogoutCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UpdateUserCommand;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.SocialCropImageManager;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.service.LogoutInteractor;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountTimelineCommand;
import com.worldventures.dreamtrips.modules.profile.api.UploadAvatarCommand;
import com.worldventures.dreamtrips.modules.profile.api.UploadCoverCommand;
import com.worldventures.dreamtrips.modules.profile.command.GetPrivateProfileCommand;
import com.worldventures.dreamtrips.modules.profile.service.ProfileInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.util.Action;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.io.File;
import java.util.Date;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import retrofit.mime.TypedFile;
import rx.Subscription;
import timber.log.Timber;

public class AccountPresenter extends ProfilePresenter<AccountPresenter.View, User> {

   public static final int AVATAR_MEDIA_REQUEST_ID = 155322;
   public static final int COVER_MEDIA_REQUEST_ID = 155323;
   public static final int DEFAULT_RATIO_X = 3;
   public static final int DEFAULT_RATIO_Y = 1;

   @Inject RootComponentsProvider rootComponentsProvider;
   @Inject LogoutInteractor logoutInteractor;
   @Inject MediaPickerManager mediaPickerManager;
   @Inject SocialCropImageManager socialCropImageManager;
   @Inject AuthInteractor authInteractor;
   @Inject ProfileInteractor profileInteractor;
   @Inject NotificationCountEventDelegate notificationCountEventDelegate;
   @Inject SnappyRepository db;

   private Subscription mediaSubscription;
   private Subscription cropSubscription;

   private String coverTempFilePath;

   @State boolean shouldReload;
   @State int mediaRequestId;

   public AccountPresenter() {
      super();
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      TrackingHelper.profile(getAccountUserId());
      //
      subscribeNotificationsBadgeUpdates();
      //
      subscribeLoadNextFeeds();
      subscribeRefreshFeeds();
      mediaSubscription = mediaPickerManager.toObservable()
            .filter(attachment -> (attachment.requestId == AVATAR_MEDIA_REQUEST_ID || attachment.requestId == COVER_MEDIA_REQUEST_ID) && attachment.chosenImages
                  .size() > 0)
            .subscribe(mediaAttachment -> {
               if (view != null) {
                  view.hideMediaPicker();
                  //
                  imageSelected(mediaAttachment);
               }
            }, error -> {
               Timber.e(error, "");
            });
      //
      socialCropImageManager.setAspectRatio(DEFAULT_RATIO_X, DEFAULT_RATIO_Y);
      connectToCroppedImageStream();
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

   private void subscribeRefreshFeeds() {
      view.bindUntilDropView(feedInteractor.getRefreshAccountTimelinePipe().observe().compose(new IoToMainComposer<>()))
            .subscribe(new ActionStateSubscriber<GetAccountTimelineCommand.Refresh>().onFail(this::refreshFeedError)
                  .onSuccess(action -> refreshFeedSucceed(action.getResult())));
   }

   private void subscribeLoadNextFeeds() {
      view.bindUntilDropView(feedInteractor.getLoadNextAccountTimelinePipe()
            .observe()
            .compose(new IoToMainComposer<>()))
            .subscribe(new ActionStateSubscriber<GetAccountTimelineCommand.LoadNext>().onFail(this::loadMoreItemsError)
                  .onSuccess(action -> addFeedItems(action.getResult())));
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
               .onFail((getCurrentUserCommand, throwable) -> this.handleError(throwable)));
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
      authInteractor.updateUserPipe().send(new UpdateUserCommand(user));
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
      authInteractor.updateUserPipe().send(new UpdateUserCommand(user));
   }

   @Override
   protected void onProfileLoaded(User user) {
      super.onProfileLoaded(user);
   }

   public void logout() {
      logoutInteractor.logoutPipe().send(new LogoutCommand());
   }

   private void connectToCroppedImageStream() {
      cropSubscription = socialCropImageManager.getCroppedImagesStream()
            .compose(new IoToMainComposer<>())
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
      if (cropSubscription != null && !cropSubscription.isUnsubscribed()) cropSubscription.unsubscribe();
   }

   @Override
   public void openBucketList() {
      shouldReload = true;
      view.openBucketList(Route.BUCKET_TABS, null);
   }

   @Override
   public void openTripImages() {
      view.openTripImages(Route.ACCOUNT_IMAGES, new TripsImagesBundle(TripImagesType.ACCOUNT_IMAGES, getAccount().getId()));
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
      view.cropImage(socialCropImageManager, image.getOriginalPath());
   }

   public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
      return socialCropImageManager.onActivityResult(requestCode, resultCode, data);
   }

   public interface View extends ProfilePresenter.View {

      void openAvatarPicker();

      void openCoverPicker();

      void updateBadgeCount(int count);

      void inject(Object object);

      void showMediaPicker(int requestId);

      void hideMediaPicker();

      void cropImage(SocialCropImageManager socialCropImageManager, String path);
   }
}
