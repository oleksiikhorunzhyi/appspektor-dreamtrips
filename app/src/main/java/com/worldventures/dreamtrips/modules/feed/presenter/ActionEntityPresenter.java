package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.feed.model.ImmutableVideoCreationModel;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.feed.model.VideoCreationModel;
import com.worldventures.dreamtrips.modules.feed.service.CreatePostBodyInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.PostDescriptionCreatedCommand;
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

public abstract class ActionEntityPresenter<V extends ActionEntityPresenter.View> extends Presenter<V> {

   @State String cachedText = "";
   @State Location location;
   @State ArrayList<PhotoCreationItem> cachedCreationItems = new ArrayList<>();
   @State Uri selectedVideoPathUri;

   @Inject EditPhotoTagsCallback editPhotoTagsCallback;
   @Inject PostLocationPickerCallback postLocationPickerCallback;
   @Inject CreatePostBodyInteractor createPostBodyInteractor;

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
            .subscribe(this::postTextChanged);
   }

   private void postTextChanged(PostDescriptionCreatedCommand action) {
      createPostBodyInteractor.getPostDescriptionPipe().clearReplays();
      cachedText = action.getResult();
      view.setText(cachedText);
   }

   protected void updateUi() {
      view.setName(getAccount().getFullName());
      view.setAvatar(getAccount());
      view.setText(cachedText);
      if (cachedCreationItems.size() != 0) {
         view.attachPhotos(cachedCreationItems);
      } else if (selectedVideoPathUri != null) {
            view.attachVideo(ImmutableVideoCreationModel.builder()
                  .state(VideoCreationModel.State.LOCAL)
                  .uri(selectedVideoPathUri)
                  .build());
      }
      invalidateDynamicViews();
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
      PhotoCreationItem item = Queryable.from(cachedCreationItems)
            .firstOrDefault(element -> element.getId() == requestId);

      if (item != null) {
         item.getCachedAddedPhotoTags().removeAll(photoTags);
         item.getCachedAddedPhotoTags().addAll(photoTags);
         item.getCachedAddedPhotoTags().removeAll(removedTags);
         item.getCachedRemovedPhotoTags().removeAll(removedTags);
         item.getCachedRemovedPhotoTags().addAll(removedTags);
         view.updatePhoto(item);
      }

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

   protected PhotoCreationItem createItemFromPhoto(Photo photo) {
      PhotoCreationItem photoCreationItem = new PhotoCreationItem();
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

   public void onLocationClicked() {
      view.openLocation(getLocation());
   }

   public interface View extends RxView {

      void attachPhotos(List<PhotoCreationItem> images);

      void attachPhoto(PhotoCreationItem image);

      void updatePhoto(PhotoCreationItem item);

      void attachVideo(VideoCreationModel videoCreationModel);

      void removeVideo(VideoCreationModel videoCreationModel);

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
