package com.worldventures.dreamtrips.modules.media_picker.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class PickerBundle implements Parcelable {

   private boolean videoPickingEnabled;
   private int pickLimit;
   private int requestId;

   private PickerBundle() {
   }

   public int getPickLimit() {
      return pickLimit;
   }

   public int getRequestId() {
      return requestId;
   }

   public boolean isVideoPickingEnabled() {
      return videoPickingEnabled;
   }

   public static class Builder {
      PickerBundle pickerBundle = new PickerBundle();

      public Builder setRequestId(int requestId) {
         pickerBundle.requestId = requestId;
         return this;
      }

      public Builder setPhotoPickLimit(int photoPickLimit) {
         pickerBundle.pickLimit = photoPickLimit;
         return this;
      }

      public Builder setVideoPickingEnabled(boolean videoPickingEnabled) {
         pickerBundle.videoPickingEnabled = videoPickingEnabled;
         return this;
      }

      public PickerBundle build() {
         return pickerBundle;
      }
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeByte(this.videoPickingEnabled ? (byte) 1 : (byte) 0);
      dest.writeInt(this.pickLimit);
      dest.writeInt(this.requestId);
   }

   protected PickerBundle(Parcel in) {
      this.videoPickingEnabled = in.readByte() != 0;
      this.pickLimit = in.readInt();
      this.requestId = in.readInt();
   }

   public static final Creator<PickerBundle> CREATOR = new Creator<PickerBundle>() {
      @Override
      public PickerBundle createFromParcel(Parcel source) {return new PickerBundle(source);}

      @Override
      public PickerBundle[] newArray(int size) {return new PickerBundle[size];}
   };
}
