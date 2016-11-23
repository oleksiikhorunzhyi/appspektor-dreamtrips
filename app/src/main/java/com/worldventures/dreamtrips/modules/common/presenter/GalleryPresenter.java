package com.worldventures.dreamtrips.modules.common.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.command.GetPhotosFromGalleryCommand;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.ArrayList;

import javax.inject.Inject;

import timber.log.Timber;

public class GalleryPresenter extends BasePickerPresenter<GalleryPresenter.View> {

   @Inject PickImageDelegate pickImageDelegate;
   @Inject MediaInteractor mediaInteractor;

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

      mediaInteractor.getPhotosFromGalleryPipe()
            .createObservableResult(new GetPhotosFromGalleryCommand(context))
            .compose(bindViewToMainComposer())
            .subscribe(getPhotosFromGalleryCommand -> {
               photos = new ArrayList<>(getPhotosFromGalleryCommand.getResult());
               view.addItems(photos);
            }, e -> Timber.e(e, "Failed to load photos from gallery"));
   }

   private void resetPickedItems() {
      Queryable.from(photos).filter(BasePhotoPickerModel::isChecked).forEachR(photo -> photo.setChecked(false));
   }

   public interface View extends BasePickerPresenter.View {

      void openFacebookAlbums();

      void checkPermissions();
   }
}
