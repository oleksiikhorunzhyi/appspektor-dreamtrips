package com.worldventures.dreamtrips.modules.common.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.feed.api.PhotoGalleryRequest;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.ArrayList;

import javax.inject.Inject;

public class GalleryPresenter extends BasePickerPresenter<GalleryPresenter.View> {

   @Inject PickImageDelegate pickImageDelegate;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      loadGallery();
   }

   public void openCamera() {
      pickImageDelegate.takePicture();
   }

   public void tryOpenCamera() {
      view.checkPermissions();
   }

   public void openFacebook() {
      view.openFacebookAlbums();
      //
      resetPickedItems();
      view.updatePickedItemsCount(0);
   }

   private void loadGallery() {
      if (photos != null && photos.size() > 0) {
         view.addItems(photos);
         return;
      }

      PhotoGalleryRequest request = new PhotoGalleryRequest(context);
      doRequest(request, photos -> {
         this.photos = new ArrayList<>(photos);
         view.addItems(this.photos);
      });
   }

   private void resetPickedItems() {
      Queryable.from(photos).filter(BasePhotoPickerModel::isChecked).forEachR(photo -> photo.setChecked(false));
   }

   public interface View extends BasePickerPresenter.View {

      void openFacebookAlbums();

      void checkPermissions();
   }
}
