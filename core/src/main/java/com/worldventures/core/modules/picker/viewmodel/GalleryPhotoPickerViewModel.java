package com.worldventures.core.modules.picker.viewmodel;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.worldventures.core.utils.Size;
import com.worldventures.core.utils.ValidationUtils;
import com.worldventures.core.modules.picker.util.adapter.MediaPickerHolderFactory;


public class GalleryPhotoPickerViewModel extends GalleryMediaPickerViewModel {
   private String absolutePath;
   private Uri uri;
   private boolean checked;
   private long dateTaken;
   private Size size;
   private long pickedTime;

   public GalleryPhotoPickerViewModel(String absolutePath) {
      this(absolutePath, 0);
   }

   public GalleryPhotoPickerViewModel(String absolutePath, Size size) {
      this(absolutePath);
      this.size = size;
   }

   public GalleryPhotoPickerViewModel(String absolutePath, long dateTaken) {
      this.absolutePath = absolutePath;
      this.uri = Uri.parse(ValidationUtils.isUrl(absolutePath) ? this.absolutePath : "file://" + this.absolutePath);
      this.dateTaken = dateTaken;
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
   public Uri getUri() {
      return uri;
   }

   @Override
   public String getAbsolutePath() {
      return absolutePath;
   }

   public long getDateTaken() {
      return dateTaken;
   }

   @Nullable
   public Size getSize() {
      return size;
   }

   @Override
   public long getPickedTime() {
      return pickedTime;
   }

   @Override
   public void setPickedTime(long pickedTime) {
      this.pickedTime = pickedTime;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      GalleryPhotoPickerViewModel that = (GalleryPhotoPickerViewModel) o;

      return absolutePath.equals(that.absolutePath);
   }

   @Override
   public int hashCode() {
      return absolutePath.hashCode();
   }
}
