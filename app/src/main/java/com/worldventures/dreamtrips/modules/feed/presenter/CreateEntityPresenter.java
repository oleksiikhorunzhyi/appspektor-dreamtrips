package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;

import com.techery.spares.annotations.State;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CreatePostCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.ScheduleCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.common.command.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerImagesProcessedEventDelegate;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.feed.model.VideoCreationModel;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.analytics.SharePostAction;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.PostCreatedCommand;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.CreatePhotoCreationItemCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.FetchLocationFromExifCommand;
import com.worldventures.dreamtrips.modules.version_check.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.version_check.service.command.ConfigurationCommand;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CreateEntityPresenter<V extends CreateEntityPresenter.View> extends ActionEntityPresenter<V> {

   private static final int MAX_PHOTOS_COUNT = 15;

   private CreateEntityBundle.Origin origin;

   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;
   @Inject MediaInteractor mediaInteractor;
   @Inject AppConfigurationInteractor appConfigurationInteractor;
   @Inject MediaPickerImagesProcessedEventDelegate mediaPickerImagesProcessedEventDelegate;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;

   @State int postInProgressId;

   private boolean mediaPickerProcessingImages;
   private int locallyProcessingImagesCount;
   private int videoLengthLimit;

   public CreateEntityPresenter(CreateEntityBundle.Origin origin) {
      this.origin = origin;
   }

   @Override
   public void takeView(V view) {
      super.takeView(view);
      if (postInProgressId != 0) {
         view.disableImagePicker();
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
      mediaPickerImagesProcessedEventDelegate.getReplayObservable()
            .compose(bindViewToMainComposer())
            .subscribe(mediaPickerProcessingImages -> {
               this.mediaPickerProcessingImages = mediaPickerProcessingImages;
               invalidateDynamicViews();
            });
      mediaPickerEventDelegate.getObservable()
            .compose(bindViewToMainComposer())
            .subscribe(this::attachMedia, error -> Timber.e(error, ""));
   }

   public void showMediaPicker() {
      view.showMediaPicker(!cachedCreationItems.isEmpty(), videoLengthLimit);
   }

   private void subscribeToVideoLength() {
      appConfigurationInteractor.configurationCommandActionPipe()
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
      boolean imageAreProcessing = mediaPickerProcessingImages || locallyProcessingImagesCount > 0;
      boolean videoSelected = selectedVideoPathUri != null;
      return !imageAreProcessing && (!isCachedTextEmpty() || cachedCreationItems.size() > 0 || videoSelected);
   }

   @Override
   public void post() {
      Observable.from(cachedCreationItems)
            .concatMap(item -> tripImagesInteractor.fetchLocationFromExifPipe()
                  .createObservableResult(new FetchLocationFromExifCommand(item.getFilePath()))
                  .map(command -> {
                     item.setLocationFromExif(command.getResult());
                     return item;
                  }))
            .toList()
            .compose(bindView())
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(creationItems ->
                  postsInteractor.createPostCompoundOperationPipe()
                        .send(new CreatePostCompoundOperationCommand(cachedText, creationItems,
                              selectedVideoPathUri != null ? selectedVideoPathUri.getPath() : null, location, origin))
            );
   }

   public void removeVideo(VideoCreationModel model) {
      selectedVideoPathUri = null;
      view.removeVideo(model);
      updateUi();
      updatePickerState();
   }

   public int getRemainingPhotosCount() {
      return MAX_PHOTOS_COUNT - cachedCreationItems.size();
   }

   public boolean removeImage(PhotoCreationItem item) {
      boolean removed = cachedCreationItems.remove(item);
      if (removed) {
         invalidateDynamicViews();
         updatePickerState();
      }
      return removed;
   }

   public void attachMedia(MediaAttachment mediaAttachment) {
      view.disableImagePicker();
      if (mediaAttachment.hasImages()) {
         attachImages(mediaAttachment.chosenImages, mediaAttachment.source);
      } else if (mediaAttachment.hasVideo()) {
         attachVideo(mediaAttachment.getChosenVideo().getUri());
      }
   }

   private void attachImages(List<PhotoPickerModel> chosenImages, MediaAttachment.Source source) {
      locallyProcessingImagesCount++;
      invalidateDynamicViews();
      Observable.from(chosenImages)
            .concatMap(photoGalleryModel -> convertPhotoCreationItem(photoGalleryModel, source))
            .compose(bindViewToMainComposer())
            .subscribe(newImage -> {
               if (ValidationUtils.isUrl(newImage.getFileUri())) {
                  mediaInteractor.copyFilePipe()
                        .createObservableResult(new CopyFileCommand(newImage.getFileUri()))
                        .compose(bindViewToMainComposer())
                        .subscribe(command -> {
                           String stringUri = command.getResult();
                           Uri uri = Uri.parse(stringUri);
                           newImage.setFilePath(uri.getPath());
                           newImage.setFileUri(stringUri);
                           onFinishedImageProcessing(newImage);
                        }, e -> {
                           locallyProcessingImagesCount--;
                           Timber.e(e, "Failed to copy file");
                        });
               } else {
                  onFinishedImageProcessing(newImage);
               }
            }, throwable -> Timber.e(throwable, ""));
   }

   private void onFinishedImageProcessing(PhotoCreationItem newImage) {
      locallyProcessingImagesCount--;
      cachedCreationItems.add(newImage);
      view.attachPhoto(newImage);
      invalidateDynamicViews();
      if (!mediaPickerProcessingImages) updatePickerState();
   }

   private Observable<PhotoCreationItem> convertPhotoCreationItem(PhotoPickerModel photoPickerModel,
         MediaAttachment.Source source) {
      return tripImagesInteractor.createPhotoCreationItemPipe()
            .createObservableResult(new CreatePhotoCreationItemCommand(photoPickerModel, source))
            .map(Command::getResult);
   }

   private void attachVideo(Uri uri) {
      selectedVideoPathUri = uri;
      updateUi();
      updatePickerState();
   }

   private void updatePickerState() {
      if ((selectedVideoPathUri == null && cachedCreationItems.isEmpty())
            || (!cachedCreationItems.isEmpty() && getRemainingPhotosCount() > 0)) {
         view.enableImagePicker();
      } else {
         view.disableImagePicker();
      }
   }

   private void closeView() {
      view.cancel();
      view = null;
   }

   public interface View extends ActionEntityPresenter.View {
      void enableImagePicker();

      void disableImagePicker();

      void showMediaPicker(boolean picturesSelected, int videoPickerLimit);
   }
}
