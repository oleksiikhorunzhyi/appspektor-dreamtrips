package com.worldventures.core.modules.picker.viewmodel;

import android.net.Uri;

import com.worldventures.core.modules.facebook.model.FacebookPhoto;
import com.worldventures.core.modules.picker.util.adapter.MediaPickerHolderFactory;

import java.util.List;


public class FacebookPhotoPickerViewModel extends FacebookMediaPickerViewModel<List<FacebookPhoto.ImageSource>> {

   private final List<FacebookPhoto.ImageSource> images;
   private final boolean checked;
   private final long pickedTime;

   public FacebookPhotoPickerViewModel(List<FacebookPhoto.ImageSource> images, boolean checked, long pickedTime) {
      super(images);
      this.images = images;
      this.checked = checked;
      this.pickedTime = pickedTime;
   }

   @Override
   public int type(MediaPickerHolderFactory typeFactory) {
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
      String imageUrl = source.size() > 2
            ? source.get(source.size() / 2 + 1).getSource()
            : source.get(0).getSource();
      return Uri.parse(imageUrl);
   }
}
