package com.worldventures.dreamtrips.modules.media_picker.model;

import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.common.view.util.Size;

public class PhotoPickerModel extends MediaPickerModelImpl {

   private Size size;

   public PhotoPickerModel() {
   }

   public PhotoPickerModel(String absolutePath, Size size) {
      this(absolutePath, 0);
      this.size = size;
   }

   public PhotoPickerModel(String absolutePath, long dateTaken) {
      super(absolutePath, dateTaken);
   }

   @Nullable
   public Size getSize() {
      return size;
   }

   @Override
   public Type getType() {
      return Type.PHOTO;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(this.size);
      dest.writeString(this.absolutePath);
      dest.writeParcelable(this.uri, 0);
      dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
      dest.writeLong(this.dateTaken);
      dest.writeLong(this.pickedTime);
   }

   protected PhotoPickerModel(Parcel in) {
      this.size = (Size) in.readSerializable();
      this.absolutePath = in.readString();
      this.uri = in.readParcelable(Uri.class.getClassLoader());
      this.checked = in.readByte() != 0;
      this.dateTaken = in.readLong();
      this.pickedTime = in.readLong();
   }

   public static final Creator<PhotoPickerModel> CREATOR = new Creator<PhotoPickerModel>() {
      @Override
      public PhotoPickerModel createFromParcel(Parcel source) {return new PhotoPickerModel(source);}

      @Override
      public PhotoPickerModel[] newArray(int size) {return new PhotoPickerModel[size];}
   };
}
