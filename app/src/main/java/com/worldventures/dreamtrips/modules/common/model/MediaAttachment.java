package com.worldventures.dreamtrips.modules.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;

import java.util.List;

public class MediaAttachment implements Parcelable {

   public final List<PhotoPickerModel> chosenImages;
   public final VideoPickerModel chosenVideo;
   public final Source source;
   public final int requestId;

   public MediaAttachment(List<PhotoPickerModel> chosenImages, Source source) {
      this(chosenImages, source, -1);
   }

   public MediaAttachment(List<PhotoPickerModel> chosenImages, Source source, int requestId) {
      this(chosenImages, null, source, requestId);
   }

   public MediaAttachment(VideoPickerModel chosenVideo, Source source, int requestId) {
      this(null, chosenVideo, source, requestId);
   }

   private MediaAttachment(List<PhotoPickerModel> chosenImages, VideoPickerModel chosenVideo, Source source, int requestId) {
      this.chosenImages = chosenImages;
      this.chosenVideo = chosenVideo;
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
      dest.writeParcelable(chosenVideo, 0);
      dest.writeInt(this.source.ordinal());
      dest.writeInt(this.requestId);
   }

   public VideoPickerModel getChosenVideo() {
      return chosenVideo;
   }

   public boolean hasImages() {
      return chosenImages != null;
   }

   public boolean hasVideo() {
      return chosenVideo != null;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      MediaAttachment that = (MediaAttachment) o;

      if (requestId != that.requestId) return false;
      if (chosenImages != null && that.chosenImages != null) return compareImages(that.chosenImages);
      if (chosenVideo != null ? !chosenVideo.equals(that.chosenVideo) : that.chosenVideo != null) return false;
      return source == that.source;
   }

   private boolean compareImages(List<PhotoPickerModel> thatChosenImages) {
      if (chosenImages.size() != thatChosenImages.size()) {
         return false;
      }

      for (PhotoPickerModel photoPickerModel : thatChosenImages) {
         if (!chosenImages.contains(photoPickerModel)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public int hashCode() {
      int result = chosenImages != null ? chosenImages.hashCode() : 0;
      result = 31 * result + (chosenVideo != null ? chosenVideo.hashCode() : 0);
      result = 31 * result + (source != null ? source.hashCode() : 0);
      result = 31 * result + requestId;
      return result;
   }

   protected MediaAttachment(Parcel in) {
      this.chosenImages = in.createTypedArrayList(PhotoPickerModel.CREATOR);
      this.chosenVideo = in.readParcelable(VideoPickerModel.class.getClassLoader());
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
