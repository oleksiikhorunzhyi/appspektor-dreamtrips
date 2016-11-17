package com.worldventures.dreamtrips.modules.feed.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyInteractor;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.command.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.analytics.SharePhotoPostAction;
import com.worldventures.dreamtrips.modules.feed.service.analytics.SharePostAction;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePhotosCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePostCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.CreatePhotoCreationItemCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.FetchLocationFromExifCommand;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class CreateEntityPresenter<V extends CreateEntityPresenter.View> extends ActionEntityPresenter<V> {

   private static final int MAX_PHOTOS_COUNT = 15;

   private CreateEntityBundle.Origin origin;

   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;
   @Inject MediaInteractor mediaInteractor;
   @Inject UploaderyInteractor uploaderyInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;

   public CreateEntityPresenter(CreateEntityBundle.Origin origin) {
      this.origin = origin;
   }

   @Override
   public void takeView(V view) {
      super.takeView(view);
      uploaderyInteractor.uploadImageActionPipe().observe()
            .compose(bindViewToMainComposer())
            .subscribe(state -> {
               PhotoCreationItem item = getPhotoCreationItemById(state.action.getFileUri());
               if (item != null) {
                  item.setStatus(state.status);
                  if (state.status == ActionState.Status.SUCCESS) {
                     item.setOriginUrl(((SimpleUploaderyCommand) state.action).getResult().response().uploaderyPhoto().location());
                     invalidateDynamicViews();
                     updatePickerState();
                  } else if (state.status == ActionState.Status.FAIL) {
                     invalidateDynamicViews();
                     updatePickerState();
                  }
                  view.updateItem(item);
               }
            }, error -> Timber.e(error, ""));
   }

   @Override
   public void onResume() {
      super.onResume();
      mediaPickerEventDelegate.getObservable()
            .compose(new IoToMainComposer<>())
            .compose(bindUntilPause())
            .subscribe(this::attachImages, error -> Timber.e(error, ""));
   }

   private PhotoCreationItem getPhotoCreationItemById(String fileUri) {
      return Queryable.from(cachedCreationItems).firstOrDefault(cachedTask -> cachedTask.getFileUri()
            .equals(fileUri));
   }

   @Override
   protected void updateUi() {
      super.updateUi();
      if (!isCachedUploadTaskEmpty()) view.attachPhotos(cachedCreationItems);
      invalidateDynamicViews();
   }

   @Override
   protected boolean isChanged() {
      return !isCachedTextEmpty() || (cachedCreationItems.size() > 0 && isEntitiesReadyToPost());
   }

   @Override
   public void post() {
      if (!isCachedTextEmpty() && isCachedUploadTaskEmpty()) {
         createPost(null);
      } else {
         postsInteractor.createPhotosPipe()
               .createObservable(new CreatePhotosCommand(cachedCreationItems, location))
               .compose(bindViewToMainComposer())
               .subscribe(new ActionStateSubscriber<CreatePhotosCommand>()
                     .onSuccess(createPhotosCommand -> createPost(createPhotosCommand.getResult()))
                     .onFail(this::handleError));
      }
   }

   private void createPost(List<Photo> photos) {
      postsInteractor.createPostPipe()
            .createObservable(new CreatePostCommand(cachedText, location, photos))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<CreatePostCommand>()
                  .onSuccess(createPostCommand -> processPostSuccess(createPostCommand.getResult()))
                  .onFail(this::handleError));
   }

   @Override
   protected void processPostSuccess(FeedEntity textualPost) {
      super.processPostSuccess(textualPost);
      if (cachedCreationItems.size() > 0) {
         Observable.from(cachedCreationItems)
               .concatMap(item -> tripImagesInteractor.fetchLocationFromExifPipe()
                     .createObservableResult(new FetchLocationFromExifCommand(item.getFilePath()))
                     .map(command -> {
                        item.setLocationFromExif(command.getResult());
                        return item;
                     }))
               .toList()
               .subscribe(creationItemsWithExif -> {
                  analyticsInteractor.analyticsActionPipe()
                        .send(SharePhotoPostAction.createPostAction((TextualPost) textualPost, creationItemsWithExif, origin));
               });
      } else {
         analyticsInteractor.analyticsActionPipe().send(SharePostAction.createPostAction((TextualPost) textualPost));
      }

      eventBus.post(new FeedItemAddedEvent(FeedItem.create(textualPost, getAccount())));
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
                  view.attachPhoto(newImage);
                  if (ValidationUtils.isUrl(newImage.getFileUri())) {
                     mediaInteractor.copyFilePipe()
                           .createObservableResult(new CopyFileCommand(context, newImage.getFileUri()))
                           .compose(bindViewToMainComposer())
                           .subscribe(command -> {
                              newImage.setFileUri(command.getResult());
                              startUpload(newImage);
                           }, e -> Timber.e(e, "Failed to copy file"));
                  } else {
                     startUpload(newImage);
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

   private boolean isEntitiesReadyToPost() {
      return Queryable.from(cachedCreationItems)
            .firstOrDefault(item -> item.getStatus() != ActionState.Status.SUCCESS) == null;
   }

   private void updatePickerState() {
      if (view == null) return;
      if (isAllAttachmentsCompleted() && getRemainingPhotosCount() > 0) {
         view.enableImagePicker();
      } else {
         view.disableImagePicker();
      }
   }

   private boolean isAllAttachmentsCompleted() {
      return Queryable.from(cachedCreationItems).count(item -> item.getStatus() == ActionState.Status.PROGRESS) == 0;
   }

   public void startUpload(PhotoCreationItem item) {
      item.setId(item.getFileUri().hashCode());
      uploaderyInteractor.uploadImageActionPipe().send(new SimpleUploaderyCommand(item.getFileUri()));
   }

   public interface View extends ActionEntityPresenter.View {

      void enableImagePicker();

      void disableImagePicker();
   }
}
