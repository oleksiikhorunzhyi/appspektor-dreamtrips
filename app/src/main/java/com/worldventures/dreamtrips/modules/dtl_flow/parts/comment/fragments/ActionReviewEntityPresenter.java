package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.common.view.util.PhotoPickerDelegate;
import com.worldventures.dreamtrips.modules.feed.service.CreatePostBodyInteractor;
import com.worldventures.dreamtrips.modules.feed.service.HashtagInteractor;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.EditPhotoTagsCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.PostLocationPickerCallback;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public abstract class ActionReviewEntityPresenter<V extends ActionReviewEntityPresenter.View> extends Presenter<V> {

   @State String cachedText = "";
   @State Location location;
   @State ArrayList<PhotoReviewCreationItem> cachedCreationItems = new ArrayList<>();

   @Inject EditPhotoTagsCallback editPhotoTagsCallback;
   @Inject PostLocationPickerCallback postLocationPickerCallback;
   @Inject HashtagInteractor hashtagInteractor;
   @Inject CreatePostBodyInteractor createPostBodyInteractor;
   @Inject PhotoPickerDelegate photoPickerDelegate;

   protected boolean photoPickerVisible;

   @Override
   public void takeView(V view) {
      super.takeView(view);
      updateUi();
      editPhotoTagsCallback.toObservable()
            .compose(bindView())
            .subscribe(bundle -> onTagSelected(bundle.requestId, bundle.addedTags, bundle.removedTags),
                  error -> Timber.e(error, ""));
      postLocationPickerCallback.toObservable()
            .compose(bindView())
            .subscribe(this::updateLocation, error -> Timber.e(error, ""));
      photoPickerDelegate.setPhotoPickerListener(new PhotoPickerLayout.PhotoPickerListener() {
         @Override
         public void onClosed() {
            photoPickerVisible = false;
         }

         @Override
         public void onOpened() {
            photoPickerVisible = true;
         }
      });
   }

   @Override
   public void onResume() {
      super.onResume();
      // we must have this subscription in onResume as during rotation
      // view of the old fragment is dropped after view for new one was created
      createPostBodyInteractor.getPostDescriptionPipe()
            .observeSuccessWithReplay()
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilPause())
            .subscribe(action -> {
               cachedText = action.getResult();
               if (view != null) {
                  view.setText(cachedText);
                  invalidateDynamicViews();
               }
               createPostBodyInteractor.getPostDescriptionPipe().clearReplays();
            }, throwable -> {
               Timber.e(throwable, "");
            });
   }

   protected void updateUi() {
      view.setName(getAccount().getFullName());
      view.setAvatar(getAccount());
      view.setText(cachedText);
   }

   public void cancelClicked() {
      if (isChanged()) view.showCancelationDialog();
      else view.cancel();
   }

   protected abstract boolean isChanged();

   public void invalidateDynamicViews() {
      if (isChanged()) view.enableButton();
      else view.disableButton();
   }

   public abstract void post();

   private void onTagSelected(long requestId, ArrayList<PhotoTag> photoTags, ArrayList<PhotoTag> removedTags) {
      PhotoReviewCreationItem item = Queryable.from(cachedCreationItems)
            .firstOrDefault(element -> element.getId() == requestId);
      //
      if (item != null) {
         item.getCachedAddedPhotoTags().removeAll(photoTags);
         item.getCachedAddedPhotoTags().addAll(photoTags);
         item.getCachedAddedPhotoTags().removeAll(removedTags);

         item.getCachedRemovedPhotoTags().removeAll(removedTags);
         item.getCachedRemovedPhotoTags().addAll(removedTags);
         //if view ==null state will be updated on attach view.
         if (view != null) {
            view.updateItem(item);
         }
      }
      //
      invalidateDynamicViews();
   }

   @Override
   public void handleError(Object action, Throwable error) {
      super.handleError(action, error);
      view.onPostError();
      view.enableButton();
   }

   @Nullable
   public Location getLocation() {
      return location;
   }

   void updateLocation(Location location) {
      this.location = location;
      invalidateDynamicViews();
      view.updateLocationButtonState();
   }

   boolean isCachedTextEmpty() {
      return TextUtils.isEmpty(cachedText);
   }

   protected PhotoReviewCreationItem createItemFromPhoto(Photo photo) {
      PhotoReviewCreationItem photoCreationItem = new PhotoReviewCreationItem();
      photoCreationItem.setTitle(photo.getTitle());
      photoCreationItem.setOriginUrl(photo.getImagePath());
      photoCreationItem.setHeight(photo.getHeight());
      photoCreationItem.setWidth(photo.getWidth());
      photoCreationItem.setLocation(photo.getLocation().getName());
      photoCreationItem.setBasePhotoTags((ArrayList<PhotoTag>) photo.getPhotoTags());
      photoCreationItem.setCanDelete(true);
      photoCreationItem.setCanEdit(true);
      return photoCreationItem;
   }

   protected void closeView() {
      view.cancel();
      view = null;
   }

   public void onLocationClicked() {
      view.openLocation(getLocation());
   }

   public interface View extends RxView {

      void attachPhotos(List<PhotoReviewCreationItem> images);

      void attachPhoto(PhotoReviewCreationItem image);

      void updateItem(PhotoReviewCreationItem item);

      void setName(String userName);

      void setAvatar(User user);

      void setText(String text);

      void cancel();

      void showCancelationDialog();

      void enableButton();

      void disableButton();

      void onPostError();

      void updateLocationButtonState();

      void openLocation(Location location);
   }

}
