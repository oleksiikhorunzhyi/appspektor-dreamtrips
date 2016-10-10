package com.worldventures.dreamtrips.modules.common.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.io.Serializable;

public class PhotoGalleryModel implements Parcelable, BasePhotoPickerModel, Serializable {

   private String absolutePath;
   private String imageUri;
   private boolean checked;
   private long dateTaken;
   private Size size;
   private long pickedTime;

   public PhotoGalleryModel(String absolutePath) {
      this(absolutePath, 0);
   }

   public PhotoGalleryModel(String absolutePath, Size size) {
      this(absolutePath);
      this.size = size;
   }

   public PhotoGalleryModel(String absolutePath, long dateTaken) {
      this.absolutePath = absolutePath;
      this.imageUri = ValidationUtils.isUrl(absolutePath) ? this.absolutePath : "file://" + this.absolutePath;
      this.dateTaken = dateTaken;
   }

   public String getAbsolutePath() {
      return absolutePath;
   }

   public String getImageUri() {
      return imageUri;
   }

   @Override
   public boolean isChecked() {
      return checked;
   }

   @Override
   public void setChecked(boolean checked) {
      this.checked = checked;
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

   public void setPickedTime(long pickedTime) {
      this.pickedTime = pickedTime;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      PhotoGalleryModel that = (PhotoGalleryModel) o;

      return absolutePath.equals(that.absolutePath);
   }

   @Override
   public int hashCode() {
      return absolutePath.hashCode();
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.absolutePath);
      dest.writeString(this.imageUri);
      dest.writeByte(checked ? (byte) 1 : (byte) 0);
      dest.writeLong(this.dateTaken);
   }

   protected PhotoGalleryModel(Parcel in) {
      this.absolutePath = in.readString();
      this.imageUri = in.readString();
      this.checked = in.readByte() != 0;
      this.dateTaken = in.readLong();
   }

   public static final Creator<PhotoGalleryModel> CREATOR = new Creator<PhotoGalleryModel>() {
      @Override
      public PhotoGalleryModel createFromParcel(Parcel source) {
         return new PhotoGalleryModel(source);
      }

      @Override
      public PhotoGalleryModel[] newArray(int size) {
         return new PhotoGalleryModel[size];
      }
   };
}
