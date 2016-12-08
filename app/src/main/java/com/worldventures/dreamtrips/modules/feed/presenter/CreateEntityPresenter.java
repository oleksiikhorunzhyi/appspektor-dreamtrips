package com.worldventures.dreamtrips.modules.feed.presenter;

import com.innahema.collections.query.functions.Converter;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.CreatePostCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.common.command.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.model.ImmutableSelectedPhoto;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.feed.model.SelectedPhoto;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.CreatePhotoCreationItemCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.FetchLocationFromExifCommand;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import rx.Observable;
import timber.log.Timber;

public class CreateEntityPresenter<V extends CreateEntityPresenter.View> extends ActionEntityPresenter<V> {

   private static final int MAX_PHOTOS_COUNT = 15;

   private CreateEntityBundle.Origin origin;

   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;
   @Inject MediaInteractor mediaInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;

   public CreateEntityPresenter(CreateEntityBundle.Origin origin) {
      this.origin = origin;
   }

   @Override
   public void takeView(V view) {
      super.takeView(view);
      postsInteractor.createPostCompoundOperationPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> {
               closeView();
               backgroundUploadingInteractor.compoundOperationsPipe()
                     .send(CompoundOperationsCommand.compoundCommandChanged(command.getResult()));
            });
   }

   @Override
   public void onResume() {
      super.onResume();
      mediaPickerEventDelegate.getObservable()
            .compose(new IoToMainComposer<>())
            .compose(bindUntilPause())
            .subscribe(this::attachImages, error -> Timber.e(error, ""));
   }

   @Override
   protected void updateUi() {
      super.updateUi();
      if (!isCachedUploadTaskEmpty()) view.attachPhotos(cachedCreationItems);
      invalidateDynamicViews();
   }

   @Override
   protected boolean isChanged() {
      return !isCachedTextEmpty() || (cachedCreationItems.size() > 0);
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
            .compose(bindViewToMainComposer())
            .subscribe(creationItems ->
                  postsInteractor.createPostCompoundOperationPipe()
                        .send(new CreatePostCompoundOperationCommand(cachedText, getSelectionPhotos(creationItems), location))
            );
   }

   private List<SelectedPhoto> getSelectionPhotos(List<PhotoCreationItem> items) {
      return Queryable.from(items)
            .map((Converter<PhotoCreationItem, SelectedPhoto>) element ->
                  ImmutableSelectedPhoto.builder()
                        .title(element.getTitle())
                        .path(element.getFilePath())
                        .locationFromExif(element.getLocationFromExif())
                        .tags(element.getCachedAddedPhotoTags())
                        .locationFromPost(location)
                        .width(element.getWidth())
                        .height(element.getHeight())
                        .build())
            .toList();

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

   public void attachImages(MediaAttachment mediaAttachment) {
      if (view == null || mediaAttachment.chosenImages == null || mediaAttachment.chosenImages.isEmpty()) return;

      view.disableImagePicker();
      imageSelected(mediaAttachment);
   }

   private void imageSelected(MediaAttachment mediaAttachment) {
      Observable.from(mediaAttachment.chosenImages)
            .concatMap(photoGalleryModel -> convertPhotoCreationItem(photoGalleryModel, mediaAttachment.source))
            .compose(bindViewToMainComposer())
            .subscribe(newImage -> {
               cachedCreationItems.add(newImage);
               if (view != null) {
                  if (ValidationUtils.isUrl(newImage.getFileUri())) {
                     mediaInteractor.copyFilePipe()
                           .createObservableResult(new CopyFileCommand(context, newImage.getFileUri()))
                           .compose(bindViewToMainComposer())
                           .subscribe(command -> {
                              view.attachPhoto(newImage);
                              newImage.setFileUri(command.getResult());
                           }, e -> Timber.e(e, "Failed to copy file"));
                  } else {
                     view.attachPhoto(newImage);
                  }
               }
            }, throwable -> Timber.e(throwable, ""));
   }

   private Observable<PhotoCreationItem> convertPhotoCreationItem(PhotoGalleryModel photoGalleryModel,
         MediaAttachment.Source source) {
      return tripImagesInteractor.createPhotoCreationItemPipe()
            .createObservableResult(new CreatePhotoCreationItemCommand(photoGalleryModel, source))
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

   public interface View extends ActionEntityPresenter.View {

      void enableImagePicker();

      void disableImagePicker();
   }
}
