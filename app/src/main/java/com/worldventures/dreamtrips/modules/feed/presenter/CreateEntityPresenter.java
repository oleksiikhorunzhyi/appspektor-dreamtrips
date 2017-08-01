package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CreatePostCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.ScheduleCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerImagesProcessedEventDelegate;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.config.service.command.ConfigurationCommand;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.model.ImmutableVideoCreationModel;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.feed.model.VideoCreationModel;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.PhotoStripDelegate;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.analytics.SharePostAction;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.PostCreatedCommand;
import com.worldventures.dreamtrips.modules.feed.view.custom.PhotoStripView;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.service.command.RecognizeFacesCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.CreatePhotoCreationItemCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.FetchLocationFromExifCommand;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CreateEntityPresenter<V extends CreateEntityPresenter.View> extends ActionEntityPresenter<V> {

   private static final int MAX_PHOTOS_COUNT = 15;
   private static final int MAX_VIDEO_COUNT = 1;

   private CreateEntityBundle.Origin origin;

   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;
   @Inject AppConfigurationInteractor appConfigurationInteractor;
   @Inject MediaPickerImagesProcessedEventDelegate mediaPickerImagesProcessedEventDelegate;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   @Inject PhotoStripDelegate photoStripDelegate;

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
      initialPhotoStripDelegate();
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

   public void initialPhotoStripDelegate() {
      photoStripDelegate.setMaxPickLimits(MAX_PHOTOS_COUNT, MAX_VIDEO_COUNT);
      photoStripDelegate.maintainPhotoStrip(view.getPhotoStrip(), bindView(), true);
      photoStripDelegate.setActions(this::mediaPickerModelChanged, this::showMediaPicker);
      photoStripDelegate.startLoadMedia();
   }

   private void mediaPickerModelChanged(MediaPickerModel model) {
      if (model.isChecked()) {
         MediaAttachment mediaAttachment;
         if (model.getType() == MediaPickerModel.Type.PHOTO) {
            mediaAttachment = new MediaAttachment((PhotoPickerModel) model, MediaAttachment.Source.PHOTO_STRIP);
         } else {
            mediaAttachment = new MediaAttachment((VideoPickerModel) model, MediaAttachment.Source.PHOTO_STRIP, 0);
         }

         attachMedia(mediaAttachment);
      } else {
         if (model.getType() == MediaPickerModel.Type.PHOTO) {
            PhotoCreationItem photoCreationItem = findPhotoPostCreation(model);
            if (photoCreationItem != null) removeImage(photoCreationItem);
         } else {
            removeVideo(ImmutableVideoCreationModel.builder()
                  .uri(model.getUri())
                  .state(VideoCreationModel.State.LOCAL)
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

         if (item.getSource() == MediaAttachment.Source.PHOTO_STRIP) {
            PhotoPickerModel samePathVideo = new PhotoPickerModel(item.getFilePath(), 0);
            photoStripDelegate.removeItem(samePathVideo);
         }
      }
   }

   public void attachMedia(MediaAttachment mediaAttachment) {
      if (!mediaAttachment.hasImages() && !mediaAttachment.hasVideo()) return;
      view.disableImagePicker();
      if (mediaAttachment.hasImages()) {
         attachImages(mediaAttachment.chosenImage, mediaAttachment.source);
      } else if (mediaAttachment.hasVideo()) {
         attachVideo(mediaAttachment.getChosenVideo().getUri());
      }
   }

   private void attachImages(PhotoPickerModel chosenImages, MediaAttachment.Source source) {
      locallyProcessingImagesCount++;
      invalidateDynamicViews();
      convertPhotoCreationItem(chosenImages, source)
            .compose(bindViewToMainComposer())
            .subscribe(this::onFinishedImageProcessing, throwable -> Timber.e(throwable, ""));
   }

   private void onFinishedImageProcessing(PhotoCreationItem newImage) {
      locallyProcessingImagesCount--;
      cachedCreationItems.add(newImage);
      view.attachPhoto(newImage);
      recognizeFaces(newImage);
      invalidateDynamicViews();
      if (!mediaPickerProcessingImages) updatePickerAndStripState();
   }

   private void recognizeFaces(PhotoCreationItem newImage) {
      mediaMetadataInteractor.recognizeFacesCommandActionPipe()
            .createObservableResult(new RecognizeFacesCommand(newImage))
            .compose(bindViewToMainComposer())
            .subscribe(updatedImage -> {
               view.updatePhoto(newImage);
            });
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
      updatePickerAndStripState();
   }

   private void updatePickerAndStripState() {
      if ((selectedVideoPathUri == null && cachedCreationItems.isEmpty())
            || (!cachedCreationItems.isEmpty() && getRemainingPhotosCount() > 0)) {
         view.enableImagePicker();
      } else {
         view.disableImagePicker();
      }

      photoStripDelegate.updateLimits(getRemainingPhotosCount(), getRemainVideoCount());
   }

   private void closeView() {
      view.cancel();
      view = null;
   }

   public interface View extends ActionEntityPresenter.View {
      void enableImagePicker();

      void disableImagePicker();

      void showMediaPicker(boolean picturesSelected, int videoPickerLimit);

      PhotoStripView getPhotoStrip();

      void removeImage(PhotoCreationItem item);
   }
}
