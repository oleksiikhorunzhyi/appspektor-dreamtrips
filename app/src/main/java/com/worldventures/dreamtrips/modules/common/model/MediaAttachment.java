package com.worldventures.dreamtrips.modules.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MediaAttachment implements Parcelable {

   public final List<PhotoGalleryModel> chosenImages;
   public final Source source;
   public final int requestId;

   public MediaAttachment(List<PhotoGalleryModel> chosenImages, Source source) {
      this(chosenImages, source, -1);
   }

   public MediaAttachment(List<PhotoGalleryModel> chosenImages, Source source, int requestId) {
      this.chosenImages = chosenImages;
      this.source = source;
      this.requestId = requestId;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeTypedList(chosenImages);
      dest.writeInt(this.source.ordinal());
      dest.writeInt(this.requestId);
   }

   protected MediaAttachment(Parcel in) {
      this.chosenImages = in.createTypedArrayList(PhotoGalleryModel.CREATOR);
      this.source = Source.values()[in.readInt()];
      this.requestId = in.readInt();
   }

   public static final Creator<MediaAttachment> CREATOR = new Creator<MediaAttachment>() {
      @Override
      public MediaAttachment createFromParcel(Parcel source) {
         return new MediaAttachment(source);
      }

      @Override
      public MediaAttachment[] newArray(int size) {
         return new MediaAttachment[size];
      }
   };

   public enum Source {
      CAMERA, GALLERY, FACEBOOK, UNKNOWN
   }
}
