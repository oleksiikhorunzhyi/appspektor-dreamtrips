package com.worldventures.dreamtrips.modules.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;

import java.util.Collections;
import java.util.List;

public class MediaAttachment implements Parcelable {

   public final PhotoPickerModel chosenImage;
   @Deprecated
   public final List<PhotoPickerModel> chosenImages;
   public final VideoPickerModel chosenVideo;
   public final Source source;
   public final int requestId;

   public MediaAttachment(PhotoPickerModel chosenImage, Source source) {
      this(chosenImage, source, -1);
   }

   public MediaAttachment(PhotoPickerModel chosenImage, Source source, int requestId) {
      this(chosenImage, null, source, requestId);
   }

   public MediaAttachment(VideoPickerModel chosenVideo, Source source, int requestId) {
      this(null, chosenVideo, source, requestId);
   }

   private MediaAttachment(PhotoPickerModel chosenImage, VideoPickerModel chosenVideo, Source source, int requestId) {
      this.chosenImage = chosenImage;
      this.chosenImages = Collections.singletonList(chosenImage);
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
      dest.writeParcelable(chosenImage, 0);
      dest.writeParcelable(chosenVideo, 0);
      dest.writeInt(this.source.ordinal());
      dest.writeInt(this.requestId);
   }

   public VideoPickerModel getChosenVideo() {
      return chosenVideo;
   }

   public boolean hasImages() {
      return chosenImage != null;
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
      if (chosenImage != null ? !chosenImage.equals(that.chosenImage) : that.chosenImage != null) return false;
      if (chosenVideo != null ? !chosenVideo.equals(that.chosenVideo) : that.chosenVideo != null) return false;
      return source == that.source;
   }

   @Override
   public int hashCode() {
      int result = chosenImage != null ? chosenImage.hashCode() : 0;
      result = 31 * result + (chosenVideo != null ? chosenVideo.hashCode() : 0);
      result = 31 * result + (source != null ? source.hashCode() : 0);
      result = 31 * result + requestId;
      return result;
   }

   protected MediaAttachment(Parcel in) {
      this.chosenImage = in.readParcelable(PhotoPickerModel.class.getClassLoader());
      this.chosenImages = Collections.singletonList(this.chosenImage);
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
      CAMERA, GALLERY, FACEBOOK, PHOTO_STRIP, UNKNOWN
   }
}
