package com.worldventures.core.modules.picker.model;

import android.net.Uri;
import android.os.Parcel;

public class VideoPickerModel extends MediaPickerModelImpl {

   private long duration;

   public VideoPickerModel(String absolutePath, long duration, long dateTaken) {
      super(absolutePath, dateTaken);
      this.duration = duration;
   }

   public VideoPickerModel(String absolutePath, long duration) {
      this(absolutePath, duration, 0);
   }

   public VideoPickerModel copy() {
      VideoPickerModel copy = new VideoPickerModel(this.absolutePath, this.dateTaken);
      copy.checked = this.checked;
      copy.duration = this.duration;
      copy.pickedTime = this.pickedTime;
      copy.uri = this.uri;
      copy.source = this.source;
      return copy;
   }

   public long getDuration() {
      return duration;
   }

   @Override
   public Type getType() {
      return Type.VIDEO;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeLong(this.duration);
      dest.writeString(this.absolutePath);
      dest.writeParcelable(this.uri, 0);
      dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
      dest.writeLong(this.dateTaken);
      dest.writeLong(this.pickedTime);
   }

   protected VideoPickerModel(Parcel in) {
      this.duration = in.readLong();
      this.absolutePath = in.readString();
      this.uri = in.readParcelable(Uri.class.getClassLoader());
      this.checked = in.readByte() != 0;
      this.dateTaken = in.readLong();
      this.pickedTime = in.readLong();
   }

   public static final Creator<VideoPickerModel> CREATOR = new Creator<VideoPickerModel>() {
      @Override
      public VideoPickerModel createFromParcel(Parcel source) {
         return new VideoPickerModel(source);
      }

      @Override
      public VideoPickerModel[] newArray(int size) {
         return new VideoPickerModel[size];
      }
   };
}
