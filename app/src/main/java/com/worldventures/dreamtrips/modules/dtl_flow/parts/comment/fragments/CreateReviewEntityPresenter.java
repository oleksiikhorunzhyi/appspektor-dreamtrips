package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments;

import android.net.Uri;

import com.innahema.collections.query.functions.Converter;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.modules.picker.command.CopyFileCommand;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.utils.FileUtils;
import com.worldventures.core.utils.ValidationUtils;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerImagesProcessedEventDelegate;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CreatePostCompoundOperationCommand;
import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.ImmutableSelectedPhoto;
import com.worldventures.dreamtrips.social.ui.feed.model.SelectedPhoto;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.CreatePostCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.FetchLocationFromExifCommand;

import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.Command;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CreateReviewEntityPresenter<V extends CreateReviewEntityPresenter.View> extends ActionReviewEntityPresenter<V> {

   private static final int MAX_PHOTOS_COUNT = 5;

   private CreateEntityBundle.Origin origin;

   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;
   @Inject MediaPickerInteractor mediaPickerInteractor;
   @Inject MediaPickerImagesProcessedEventDelegate mediaPickerImagesProcessedEventDelegate;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   @Inject BackStackDelegate backStackDelegate;

   @State int postInProgressId;

   private boolean mediaPickerProcessingImages;
   private int locallyProcessingImagesCount;

   public CreateReviewEntityPresenter(CreateEntityBundle.Origin origin) {
      this.origin = origin;
   }

   @Override
   public void takeView(V view) {
      super.takeView(view);
      if (postInProgressId != 0) {
         view.disableImagePicker();
         view.disableButton();
      }
      mediaPickerImagesProcessedEventDelegate.getReplayObservable()
            .compose(bindViewToMainComposer())
            .subscribe(mediaPickerProcessingImages -> {
               this.mediaPickerProcessingImages = mediaPickerProcessingImages;
               invalidateDynamicViews();
            });
      mediaPickerEventDelegate.getObservable()
            .compose(bindViewToMainComposer())
            .subscribe(this::attachImages, error -> Timber.e(error, ""));
   }

   @Override
   protected void updateUi() {
      super.updateUi();
      if (!isCachedUploadTaskEmpty()) view.attachPhotos(cachedCreationItems);
      invalidateDynamicViews();
   }

   private void createTextualPost(PostCompoundOperationModel postCompoundOperationModel) {
      postInProgressId = postCompoundOperationModel.id();
      postsInteractor.createPostPipe().send(new CreatePostCommand(postCompoundOperationModel));
   }

   @Override
   protected boolean isChanged() {
      boolean imageAreProcessing = mediaPickerProcessingImages || locallyProcessingImagesCount > 0;
      boolean imagesAreFullyLoaded = cachedCreationItems.size() > 0 && !imageAreProcessing;
      return !isCachedTextEmpty() && !imageAreProcessing || imagesAreFullyLoaded;
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
                        .send(new CreatePostCompoundOperationCommand(cachedText, getSelectionPhotos(creationItems),
                              origin, location))
            );
   }

   private List<SelectedPhoto> getSelectionPhotos(List<PhotoReviewCreationItem> items) {
      return Queryable.from(items)
            .map((Converter<PhotoReviewCreationItem, SelectedPhoto>) element ->
                  ImmutableSelectedPhoto.builder()
                        .title(element.getTitle())
                        .path(element.getFilePath())
                        .locationFromExif(element.getLocationFromExif())
                        .tags(element.getCachedAddedPhotoTags())
                        .locationFromPost(location)
                        .source(element.getSource())
                        .size(FileUtils.getFileSize(element.getFilePath()))
                        .width(element.getWidth())
                        .height(element.getHeight())
                        .build())
            .toList();
   }

   public int getRemainingPhotosCount() {
      return MAX_PHOTOS_COUNT - cachedCreationItems.size();
   }

   public boolean removeImage(PhotoReviewCreationItem item) {
      try {
         cachedCreationItems.remove(item);
      } catch (Exception e){
         e.printStackTrace();
         return false;
      }
      return true;


   }

   public void attachImages(MediaPickerAttachment mediaAttachment) {
      if (view == null || !mediaAttachment.hasImages()) return;

      view.disableImagePicker();
      imageSelected(mediaAttachment);
   }

   private void imageSelected(MediaPickerAttachment mediaAttachment) {
      locallyProcessingImagesCount++;
      invalidateDynamicViews();
      Observable.from(mediaAttachment.getChosenImages())
            .concatMap(this::convertPhotoCreationItem)
            .compose(bindViewToMainComposer())
            .subscribe(newImage -> {
               if (ValidationUtils.isUrl(newImage.getFileUri())) {
                  mediaPickerInteractor.copyFilePipe()
                        .createObservableResult(new CopyFileCommand(context, newImage.getFileUri()))
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

   private void onFinishedImageProcessing(PhotoReviewCreationItem newImage) {
      locallyProcessingImagesCount--;
      cachedCreationItems.add(newImage);
      view.attachPhoto(newImage);
      invalidateDynamicViews();
      if (!mediaPickerProcessingImages) {
         updatePickerState();
      }
   }

   private Observable<PhotoReviewCreationItem> convertPhotoCreationItem(PhotoPickerModel photoGalleryModel) {
      return tripImagesInteractor.createReviewPhotoCreationItemPipe()
            .createObservableResult(new CreateReviewPhotoCreationItemCommand(photoGalleryModel))
            .map(Command::getResult);
   }

   private boolean isCachedUploadTaskEmpty() {
      return cachedCreationItems.size() == 0;
   }

   private void updatePickerState() {
      if (getRemainingPhotosCount() > 0) {
         view.enableImagePicker();
      } else {
         view.disableImagePicker();
      }
   }

   public interface View extends ActionReviewEntityPresenter.View {

      void enableImagePicker();

      void disableImagePicker();
   }
}
