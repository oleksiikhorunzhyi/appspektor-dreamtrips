package com.worldventures.dreamtrips.modules.common.model;


import android.annotation.Nullable;
import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModelImpl;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaPickerAttachment implements Parcelable {
   public final int requestId;
   private final List<MediaPickerModelImpl> chosenMedia = new ArrayList<>();

   public MediaPickerAttachment() {
      this(null, null, -1);
   }

   public MediaPickerAttachment(int requestId) {
      this(null, null, requestId);
   }

   public MediaPickerAttachment(List<PhotoPickerModel> chosenImages, int requestId) {
      this(chosenImages, null, requestId);
   }

   public MediaPickerAttachment(VideoPickerModel chosenVideo, int requestId) {
      this(null, Collections.singletonList(chosenVideo), requestId);
   }

   private MediaPickerAttachment(@Nullable List<PhotoPickerModel> chosenImages, @Nullable List<VideoPickerModel> chosenVideos, int requestId) {
      this.requestId = requestId;
      if (chosenImages != null) {
         chosenMedia.addAll(chosenImages);
      }
      if (chosenVideos != null) {
         chosenMedia.addAll(chosenVideos);
      }
   }

   public void addMedia(MediaPickerModelImpl media) {
      chosenMedia.add(media);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeList(chosenMedia);
      dest.writeInt(this.requestId);
   }

   public boolean hasImages() {
      return !fetchImages().isEmpty();
   }

   public List<PhotoPickerModel> getChosenImages() {
      return fetchImages();
   }

   private List<PhotoPickerModel> fetchImages() {
      final List<PhotoPickerModel> images = new ArrayList<>();
      for (MediaPickerModelImpl model : chosenMedia) {
         if (model.getType() == MediaPickerModel.Type.PHOTO) {
            images.add((PhotoPickerModel) model);
         }
      }
      return images;
   }

   public boolean hasVideo() {
      return fetchVideo() != null;
   }

   public VideoPickerModel getChosenVideo() {
      return fetchVideo();
   }

   private VideoPickerModel fetchVideo() {
      VideoPickerModel video = null;
      for (MediaPickerModelImpl model : chosenMedia) {
         if (model.getType() == MediaPickerModel.Type.VIDEO) {
            video = (VideoPickerModel) model;
            break;
         }
      }
      return video;
   }

   public boolean isEmpty() {
      return chosenMedia.isEmpty();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      MediaPickerAttachment that = (MediaPickerAttachment) o;

      if (requestId != that.requestId) return false;
      return chosenMedia.isEmpty() != that.chosenMedia.isEmpty() && compareMedia(that.chosenMedia);
   }

   private boolean compareMedia(List<MediaPickerModelImpl> thatChosenMedia) {
      if (chosenMedia.size() != thatChosenMedia.size()) {
         return false;
      }

      for (MediaPickerModelImpl photoPickerModel : thatChosenMedia) {
         if (!chosenMedia.contains(photoPickerModel)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public int hashCode() {
      int result = chosenMedia.hashCode();
      result = 31 * result + requestId;
      return result;
   }

   protected MediaPickerAttachment(Parcel in) {
      in.readList(chosenMedia, MediaPickerModelImpl.class.getClassLoader());
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
