package com.worldventures.dreamtrips.modules.common.view.util;

import android.os.Bundle;
import android.view.View;

import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;

import java.util.List;

public class PhotoPickerDelegate {

   private PhotoPickerLayout photoPickerLayout;
   //
   private SelectedPhotosProvider selectedPhotosProvider;
   private PhotoPickerLayout.OnDoneClickListener doneClickListener;

   public boolean isMultiPickEnabled() {
      return photoPickerLayout.isMultiPickEnabled();
   }

   public int getPickLimit() {
      return photoPickerLayout.getPickLimit();
   }

   public void attachScrollableView(View view) {
      if (photoPickerLayout != null) photoPickerLayout.setScrollableView(view);
   }

   public void openFacebookAlbums() {
      photoPickerLayout.openFacebookAlbums();
   }

   public void openFacebookPhoto(Bundle bundle) {
      photoPickerLayout.openFacebookPhoto(bundle);
   }

   public void updatePickedItemsCount(int count) {
      photoPickerLayout.updatePickedItemsCount(count);
   }

   public void setupPhotoPickerLayout(PhotoPickerLayout photoPickerLayout) {
      this.photoPickerLayout = photoPickerLayout;
   }

   public void onDone() {
      if (doneClickListener != null && selectedPhotosProvider != null) {
         doneClickListener.onDone(selectedPhotosProvider.provideSelectedPhotos(), selectedPhotosProvider.getType());
      }
   }

   public void setDoneClickListener(PhotoPickerLayout.OnDoneClickListener doneClickListener) {
      this.doneClickListener = doneClickListener;
   }

   public void setSelectedPhotosProvider(SelectedPhotosProvider selectedPhotosProvider) {
      this.selectedPhotosProvider = selectedPhotosProvider;
   }

   public interface SelectedPhotosProvider {

      List<BasePhotoPickerModel> provideSelectedPhotos();

      int getType();
   }

}
