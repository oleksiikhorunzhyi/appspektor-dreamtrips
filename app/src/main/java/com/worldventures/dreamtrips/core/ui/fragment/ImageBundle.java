package com.worldventures.dreamtrips.core.ui.fragment;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageBundle<T extends ImagePathHolder> implements Parcelable {

   public final T imagePathHolder;
   public final boolean fullScreen;

   public ImageBundle(T imagePathHolder) {
      this(imagePathHolder, false);
   }

   public ImageBundle(T imagePathHolder, boolean fullScreen) {
      this.imagePathHolder = imagePathHolder;
      this.fullScreen = fullScreen;
   }


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(this.imagePathHolder);
      dest.writeByte(fullScreen ? (byte) 1 : (byte) 0);
   }

   protected ImageBundle(Parcel in) {
      this.imagePathHolder = (T) in.readSerializable();
      this.fullScreen = in.readByte() != 0;
   }

   public static final Creator<ImageBundle> CREATOR = new Creator<ImageBundle>() {
      public ImageBundle createFromParcel(Parcel source) {
         return new ImageBundle(source);
      }

      public ImageBundle[] newArray(int size) {
         return new ImageBundle[size];
      }
   };
}
