package com.worldventures.dreamtrips.social.ui.feed.presenter;

import android.net.Uri;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.modules.picker.helper.PickerPermissionChecker;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.model.MediaPickerModel;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.modules.picker.model.VideoPickerModel;
import com.worldventures.core.ui.util.permission.PermissionUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.config.service.command.ConfigurationCommand;
import com.worldventures.dreamtrips.modules.media_picker.service.command.RecognizeFacesCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostBody;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.ScheduleCompoundOperationCommand;
import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.ImmutableVideoCreationModel;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.social.ui.feed.model.VideoCreationModel;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.PhotoStripDelegate;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.analytics.SharePostAction;
import com.worldventures.dreamtrips.social.ui.feed.service.command.CreatePostCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.PostCreatedCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ProcessAttachmentsAndPost;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.PhotoStripView;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.CreatePhotoCreationItemCommand;
import com.worldventures.dreamtrips.social.ui.util.PermissionUIComponent;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class CreateEntityPresenter<V extends CreateEntityPresenter.View> extends ActionEntityPresenter<V> {

   private static final int MAX_PHOTOS_COUNT = 15;
   private static final int MAX_VIDEO_COUNT = 1;

   private CreateEntityBundle.Origin origin;

   @Inject AppConfigurationInteractor appConfigurationInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   @Inject PhotoStripDelegate photoStripDelegate;
   @Inject PickerPermissionChecker pickerPermissionChecker;
   @Inject PermissionUtils permissionUtils;

   @State int postInProgressId;
   @State int videoLengthLimit;

   public CreateEntityPresenter(CreateEntityBundle.Origin origin) {
      this.origin = origin;
   }

   @Override
   public void onInjected() {
      super.onInjected();
      pickerPermissionChecker.registerCallback(
            this::showPickerSkipPermission,
            () -> view.showPermissionDenied(PickerPermissionChecker.PERMISSIONS),
            () -> view.showPermissionExplanationText(PickerPermissionChecker.PERMISSIONS));
   }

   @Override
   public void takeView(V view) {
      super.takeView(view);
      initPhotoStripDelegate();
      if (postInProgressId != 0) {
         view.setEnabledImagePicker(false);
         view.disableButton();
      }
      subscribeToVideoLength();
      postsInteractor.createPostCompoundOperationPipe()
            .observeSuccess()
            .map(Command::getResult)
            .compose(bindViewToMainComposer())
            .subscribe(postCompoundOperationModel -> {
               if (postCompoundOperationModel.type() == PostBody.Type.TEXT) {
                  createTextualPost(postCompoundOperationModel);
               } else {
                  closeView();
                  backgroundUploadingInteractor.scheduleOperationPipe()
                        .send(new ScheduleCompoundOperationCommand(postCompoundOperationModel));
               }
            });
      postsInteractor.createPostPipe()
            .observe()
            .filter(state -> state.action.getId() == postInProgressId)
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<CreatePostCommand>()
                  .onFail(this::handleError)
                  .onSuccess(command -> {
                     postsInteractor.postCreatedPipe().send(new PostCreatedCommand(command.getResult()));
                     analyticsInteractor.analyticsActionPipe()
                           .send(SharePostAction.createPostAction(command.getResult()));
                     closeView();
                  }));
   }

   @Override
   protected void updateDescription() {
      if (origin == CreateEntityBundle.Origin.FEED) {
         super.updateDescription();
      }
   }

   public void initPhotoStripDelegate() {
      photoStripDelegate.setMaxPickLimits(MAX_PHOTOS_COUNT, MAX_VIDEO_COUNT);
      photoStripDelegate.maintainPhotoStrip(view.getPhotoStrip(), bindView(), true);
      photoStripDelegate.setActions(this::mediaPickerModelChanged, this::showMediaPicker);
      photoStripDelegate.startLoadMedia();
      photoStripDelegate.subscribeToCameraSubscriptions();
   }

   private void mediaPickerModelChanged(MediaPickerModel model) {
      if (model.isChecked()) {
         MediaPickerAttachment mediaAttachment;
         mediaAttachment = model.getType() == MediaPickerModel.Type.PHOTO
               ? new MediaPickerAttachment(Collections.singletonList((PhotoPickerModel) model), -1)
               : new MediaPickerAttachment((VideoPickerModel) model, -1);

         attachMedia(mediaAttachment);
      } else {
         if (model.getType() == MediaPickerModel.Type.PHOTO) {
            PhotoCreationItem photoCreationItem = findPhotoPostCreation(model);
            if (photoCreationItem != null) {
               removeImage(photoCreationItem);
            }
         } else {
            removeVideo(ImmutableVideoCreationModel.builder()
                  .uri(model.getUri())
                  .state(VideoCreationModel.State.LOCAL)
                  .canDelete(canDeleteVideo())
                  .build());
         }
      }
   }

   private PhotoCreationItem findPhotoPostCreation(MediaPickerModel photoPickerModel) {
      return Queryable.from(cachedCreationItems)
            .filter(photoCreationItem -> photoCreationItem.getFileUri().equals(photoPickerModel.getUri().toString()))
            .firstOrDefault();
   }

   public void showMediaPicker() {
      photoStripDelegate.unsubscribeFromCameraSubscriptions();
      pickerPermissionChecker.checkPermission();
   }

   public void showPickerSkipPermission() {
      view.showMediaPicker(getRemainingPhotosCount(), getRemainVideoCount(), videoLengthLimit);
   }

   public void recheckPermission(String[] permissions, boolean userAnswer) {
      if (permissionUtils.equals(permissions, PickerPermissionChecker.PERMISSIONS)) {
         pickerPermissionChecker.recheckPermission(userAnswer);
      }
   }

   private void subscribeToVideoLength() {
      appConfigurationInteractor.getConfigurationPipe()
            .createObservableResult(new ConfigurationCommand())
            .compose(bindViewToMainComposer())
            .map(ConfigurationCommand::getVideoMaxLength)
            .subscribe(maxLength -> videoLengthLimit = maxLength);
   }

   private void createTextualPost(PostCompoundOperationModel postCompoundOperationModel) {
      postInProgressId = postCompoundOperationModel.id();
      postsInteractor.createPostPipe().send(new CreatePostCommand(postCompoundOperationModel));
   }

   @Override
   protected boolean isChanged() {
      boolean videoSelected = selectedVideoPathUri != null;
      return !isCachedTextEmpty() || cachedCreationItems.size() > 0 || videoSelected;
   }

   @Override
   public void post() {
      postsInteractor.processAttachmentsAndPostPipe()
            .send(new ProcessAttachmentsAndPost(cachedText, cachedCreationItems, selectedVideoPathUri, location, origin));
   }

   public void removeVideo(VideoCreationModel model) {
      selectedVideoPathUri = null;
      view.removeVideo(model);
      updateUi();
      updatePickerAndStripState();

      VideoPickerModel samePathVideo = new VideoPickerModel(model.uri().getPath(), 0);
      photoStripDelegate.removeItem(samePathVideo);
   }

   public int getRemainingPhotosCount() {
      return MAX_PHOTOS_COUNT - cachedCreationItems.size();
   }

   private int getRemainVideoCount() {
      return selectedVideoPathUri == null ? MAX_VIDEO_COUNT : 0;
   }

   public void removeImage(PhotoCreationItem item) {
      boolean removed = cachedCreationItems.remove(item);
      if (removed) {
         invalidateDynamicViews();
         updatePickerAndStripState();
         view.removeImage(item);

         if (item.getSource() == MediaPickerAttachment.Source.PHOTO_STRIP) {
            PhotoPickerModel samePathVideo = new PhotoPickerModel(item.getFilePath(), 0);
            photoStripDelegate.removeItem(samePathVideo);
         }
      }
   }

   public void attachMedia(MediaPickerAttachment mediaPickerAttachment) {
      if (!mediaPickerAttachment.hasImages() && !mediaPickerAttachment.hasVideo()) {
         return;
      }

      if (!cachedCreationItems.isEmpty() && mediaPickerAttachment.hasVideo() ||
            (selectedVideoPathUri != null && mediaPickerAttachment.hasImages())) {
         view.informUser(R.string.picker_two_media_type_error);
         return;
      }
      view.setEnabledImagePicker(false);
      if (mediaPickerAttachment.hasImages()) {
         attachImages(mediaPickerAttachment.getChosenImages());
      } else if (mediaPickerAttachment.hasVideo()) {
         attachVideo(mediaPickerAttachment.getChosenVideo().getUri());
      }
   }

   private void attachImages(List<PhotoPickerModel> chosenImages) {
      invalidateDynamicViews();
      Observable.from(chosenImages)
            .flatMap(this::convertPhotoCreationItem)
            .toList()
            .compose(bindViewToMainComposer())
            .subscribe(this::onFinishedImageProcessing, throwable -> Timber.e(throwable, ""));
   }

   private void onFinishedImageProcessing(List<PhotoCreationItem> newImages) {
      cachedCreationItems.addAll(newImages);
      view.attachPhotos(newImages);
      recognizeFaces(newImages);
      invalidateDynamicViews();
      updatePickerAndStripState();
   }

   private void recognizeFaces(List<PhotoCreationItem> newImages) {
      Observable.from(newImages)
            .flatMap(newImage -> mediaMetadataInteractor.recognizeFacesCommandActionPipe()
                  .createObservableResult(new RecognizeFacesCommand(newImage)))
            .compose(bindViewToMainComposer())
            .map(Command::getResult)
            .subscribe(view::updatePhoto);
   }

   private Observable<PhotoCreationItem> convertPhotoCreationItem(PhotoPickerModel photoPickerModel) {
      return tripImagesInteractor.createPhotoCreationItemPipe()
            .createObservableResult(new CreatePhotoCreationItemCommand(photoPickerModel, photoPickerModel.getSource()))
            .map(Command::getResult);
   }

   private void attachVideo(Uri uri) {
      selectedVideoPathUri = uri;
      updateUi();
      updatePickerAndStripState();
   }

   private void updatePickerAndStripState() {
      view.setEnabledImagePicker((selectedVideoPathUri == null && cachedCreationItems.isEmpty())
            || (!cachedCreationItems.isEmpty() && getRemainingPhotosCount() > 0));
      photoStripDelegate.updateLimits(getRemainingPhotosCount(), getRemainVideoCount());
   }

   private void closeView() {
      view.cancel();
      view = null;
   }

   public interface View extends ActionEntityPresenter.View, PermissionUIComponent {
      void setEnabledImagePicker(boolean enabled);

      void showMediaPicker(int photoPickLimit, int videoPickLimit, int maxVideoDuration);

      PhotoStripView getPhotoStrip();

      void removeImage(PhotoCreationItem item);
   }
}
