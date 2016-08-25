package com.worldventures.dreamtrips.modules.feed.presenter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
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
import io.techery.janet.ActionState;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public abstract class ActionEntityPresenter<V extends ActionEntityPresenter.View> extends Presenter<V> {

   @State String cachedText = "";
   @State Location location;
   @State ArrayList<PhotoCreationItem> cachedCreationItems = new ArrayList<>();

   @Inject EditPhotoTagsCallback editPhotoTagsCallback;
   @Inject PostLocationPickerCallback postLocationPickerCallback;
   @Inject HashtagInteractor hashtagInteractor;
   @Inject CreatePostBodyInteractor createPostBodyInteractor;
   private Subscription editTagsSubscription;
   private Subscription locationPickerSubscription;
   private Subscription postDescriptionSubscription;

   @Override
   public void takeView(V view) {
      super.takeView(view);
      updateUi();
      //
      editTagsSubscription = editPhotoTagsCallback.toObservable().subscribe(bundle -> {
         onTagSelected(bundle.requestId, bundle.addedTags, bundle.removedTags);
      }, error -> {
         Timber.e(error, "");
      });

      locationPickerSubscription = postLocationPickerCallback.toObservable().subscribe((loc) -> {
         updateLocation(loc);
      }, error -> {
         Timber.e(error, "");
      });
   }

   @Override
   public void onResume() {
      super.onResume();
      // we must have this subscription in onResume as during rotation
      // view of the old fragment is dropped after view for new one was created
      postDescriptionSubscription = createPostBodyInteractor.getPostDescriptionPipe()
            .observeSuccessWithReplay()
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
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

   @Override
   public void onPause() {
      super.onPause();
      if (postDescriptionSubscription != null && !postDescriptionSubscription.isUnsubscribed()) {
         postDescriptionSubscription.unsubscribe();
      }
   }

   @Override
   public void dropView() {
      super.dropView();
      //
      if (editTagsSubscription != null && !editTagsSubscription.isUnsubscribed()) editTagsSubscription.unsubscribe();

      if (locationPickerSubscription != null && !locationPickerSubscription.isUnsubscribed())
         locationPickerSubscription.unsubscribe();
   }

   protected void updateUi() {
      view.setName(getAccount().getFullName());
      view.setAvatar(getAccount());
      view.setText(cachedText);
   }

   public void cancelClicked() {
      if (view != null) {
         if (isChanged()) {
            view.showCancelationDialog();
         } else {
            view.cancel();
         }
      }
   }

   protected abstract boolean isChanged();

   public void invalidateDynamicViews() {
      if (isChanged()) {
         view.enableButton();
      } else {
         view.disableButton();
      }
   }

   public abstract void post();

   public void onTagSelected(long requestId, ArrayList<PhotoTag> photoTags, ArrayList<PhotoTag> removedTags) {
      PhotoCreationItem item = Queryable.from(cachedCreationItems)
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
   public void handleError(SpiceException error) {
      super.handleError(error);
      view.onPostError();
      view.enableButton();
   }

   @Nullable
   public Location getLocation() {
      return location;
   }

   protected void updateLocation(Location location) {
      this.location = location;
      invalidateDynamicViews();
      view.updateLocationButtonState();
   }

   protected boolean isCachedTextEmpty() {
      return TextUtils.isEmpty(cachedText);
   }

   protected void processPostSuccess(FeedEntity feedEntity) {
      closeView();
   }

   protected PhotoCreationItem createItemFromPhoto(Photo photo) {
      PhotoCreationItem photoCreationItem = new PhotoCreationItem();
      photoCreationItem.setTitle(photo.getTitle());
      photoCreationItem.setOriginUrl(photo.getImagePath());
      photoCreationItem.setHeight(photo.getHeight());
      photoCreationItem.setWidth(photo.getWidth());
      photoCreationItem.setStatus(ActionState.Status.SUCCESS);
      photoCreationItem.setLocation(photo.getLocation().getName());
      photoCreationItem.setBasePhotoTags((ArrayList<PhotoTag>) photo.getPhotoTags());
      photoCreationItem.setCanDelete(true);
      photoCreationItem.setCanEdit(true);
      return photoCreationItem;
   }

   private void closeView() {
      view.cancel();
      view = null;
   }

   public void onLocationClicked() {
      view.openLocation(getLocation());
   }

   public interface View extends RxView {

      void attachPhotos(List<PhotoCreationItem> images);

      void attachPhoto(PhotoCreationItem image);

      void updateItem(PhotoCreationItem item);

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
