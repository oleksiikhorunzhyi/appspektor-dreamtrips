package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;
import android.util.Pair;

import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.EditPhotoWithTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

public class EditPhotoPresenter extends ActionEntityPresenter<EditPhotoPresenter.View> {

   private Photo photo;
   private Subscription faceSuggestionSubscription;
   @Inject TripImagesInteractor tripImagesInteractor;

   public EditPhotoPresenter(Photo photo) {
      this.photo = photo;
   }

   @Override
   public void takeView(View view) {
      if (cachedCreationItems.size() == 0) cachedCreationItems.add(createItemFromPhoto(photo));
      //
      super.takeView(view);
      //
      if (location == null) updateLocation(photo.getLocation());
   }

   @Override
   protected void updateUi() {
      view.setName(getAccount().getFullName());
      view.setAvatar(getAccount());
      view.attachPhotos(cachedCreationItems);
   }

   @Override
   public void onResume() {
      super.onResume();
      faceSuggestionSubscription = Observable.from(cachedCreationItems)
            .flatMap(photoCreationItem -> ImageUtils.getRecognizedFaces(context, ImageUtils.getBitmap(context, Uri.parse(photoCreationItem
                  .getOriginUrl()), 300, 300))
                  .flatMap(photoTags -> Observable.just(new Pair<PhotoCreationItem, ArrayList>(photoCreationItem, photoTags))))
            .compose(new IoToMainComposer<>())
            .subscribe(pair -> {
               pair.first.setSuggestions(pair.second);
               if (view != null) {
                  view.updateItem(pair.first);
               }
            }, e -> {
               Timber.e(e, "");
            });
   }

   @Override
   public void onStop() {
      super.onStop();
      if (faceSuggestionSubscription != null && !faceSuggestionSubscription.isUnsubscribed()) {
         faceSuggestionSubscription.unsubscribe();
      }
   }

   @Override
   protected boolean isChanged() {
      return isLocationChanged() || isTagsChanged() || isTitleChanged();
   }

   private boolean isLocationChanged() {
      return !photo.getLocation().equals(location);
   }

   private boolean isTagsChanged() {
      PhotoCreationItem item = cachedCreationItems.get(0);
      return item.getCachedAddedPhotoTags().size() > 0 || item.getCachedRemovedPhotoTags().size() > 0;
   }

   private boolean isTitleChanged() {
      PhotoCreationItem item = cachedCreationItems.get(0);
      return !item.getTitle().equals(photo.getTitle());
   }

   @Override
   public void post() {
      updatePhoto();
   }

   @Override
   protected PhotoCreationItem createItemFromPhoto(Photo photo) {
      PhotoCreationItem item = super.createItemFromPhoto(photo);
      item.setCanDelete(false);
      return item;
   }

   private void updatePhoto() {
      UploadTask uploadTask = new UploadTask();
      PhotoCreationItem creationItem = cachedCreationItems.get(0);
      uploadTask.setTitle(creationItem.getTitle());
      uploadTask.setLocationName(location.getName());
      uploadTask.setLatitude((float) location.getLat());
      uploadTask.setLongitude((float) location.getLng());
      uploadTask.setShotAt(photo.getShotAt());
      tripImagesInteractor.editPhotoWithTagsCommandActionPipe()
            .createObservable(new EditPhotoWithTagsCommand(photo.getUid(), uploadTask,
                  creationItem.getCachedAddedPhotoTags(), creationItem.getCachedRemovedPhotoTags()))
            .compose(new IoToMainComposer<>())
            .subscribe(new ActionStateSubscriber<EditPhotoWithTagsCommand>()
               .onSuccess(command -> view.cancel())
            .onFail((editPhotoWithTagsCommand, throwable) -> {
               handleError(editPhotoWithTagsCommand, throwable);
               view.onPostError();
            }));
   }

   public interface View extends ActionEntityPresenter.View {

   }
}
