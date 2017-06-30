package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos;

import android.net.Uri;

import com.worldventures.dreamtrips.modules.facebook.model.FacebookPhoto;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerHolderFactory;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.WalletFacebookPickerModel;

import java.util.List;


public class WalletFacebookPhotoModel extends WalletFacebookPickerModel<List<FacebookPhoto.ImageSource>> {

   private List<FacebookPhoto.ImageSource> images;

   private boolean checked;
   private long pickedTime;

   public WalletFacebookPhotoModel(List<FacebookPhoto.ImageSource> images, boolean checked, long pickedTime) {
      super(images);
      this.images = images;
      this.checked = checked;
      this.pickedTime = pickedTime;
   }

   @Override
   public int type(WalletPickerHolderFactory typeFactory) {
      return typeFactory.type(this);
   }

   @Override
   public Type getType() {
      return Type.PHOTO;
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
   public String getAbsolutePath() {
      return images.get(0).getSource();
   }

   @Override
   public long getPickedTime() {
      return pickedTime;
   }

   @Override
   public long getDateTaken() {
      return 0;
   }

   @Override
   public void setPickedTime(long pickedTime) {
      this.pickedTime = pickedTime;
   }

   @Override
   public Uri getUriFromSource(List<FacebookPhoto.ImageSource> source) {
      String imageUrl = (source.size() > 2)
            ? source.get(source.size() / 2 + 1).getSource()
            : source.get(0).getSource();
      return Uri.parse(imageUrl);
   }
}
