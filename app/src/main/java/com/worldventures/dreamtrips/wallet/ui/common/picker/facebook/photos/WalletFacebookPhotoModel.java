package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos;

import com.worldventures.dreamtrips.modules.facebook.model.FacebookPhoto;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerHolderFactory;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.WalletFacebookPickerModel;

import java.util.List;


public class WalletFacebookPhotoModel extends WalletFacebookPickerModel {

   private List<FacebookPhoto.ImageSource> images;

   private boolean checked;
   private long pickedTime;
   private String imageUri;

   public WalletFacebookPhotoModel(List<FacebookPhoto.ImageSource> images, boolean checked, long pickedTime) {
      this.images = images;
      this.checked = checked;
      this.pickedTime = pickedTime;
      if (images.size() > 2) {
         imageUri = images.get(images.size() / 2 + 1).getSource();
      } else {
         imageUri = images.get(0).getSource();
      }
   }

   public WalletFacebookPhotoModel(String imageUri) {
      this.imageUri = imageUri;
   }

   @Override
   public int type(WalletPickerHolderFactory typeFactory) {
      return typeFactory.type(this);
   }

   @Override
   public boolean isChecked() {
      return checked;
   }

   @Override
   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   @Override
   public String getImageUri() {
      return imageUri;
   }

   @Override
   public String getAbsolutePath() {
      return images.get(0).getSource();
   }

   @Override
   public long getPickedTime() {
      return pickedTime;
   }

   @Override
   public void setPickedTime(long pickedTime) {
      this.pickedTime = pickedTime;
   }
}
