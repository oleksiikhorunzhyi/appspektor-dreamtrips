package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.feed.model.video.Video;

public class VideoMediaEntity extends BaseMediaEntity<Video> {

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.type == null ? -1 : this.type.ordinal());
      dest.writeParcelable(this.item, 0);
   }

   public VideoMediaEntity() {
      type = TripImageType.VIDEO;
   }

   protected VideoMediaEntity(Parcel in) {
      int tmpType = in.readInt();
      this.type = tmpType == -1 ? null : TripImageType.values()[tmpType];
      this.item = in.readParcelable(Video.class.getClassLoader());
   }

   public static final Creator<VideoMediaEntity> CREATOR = new Creator<VideoMediaEntity>() {
      @Override
      public VideoMediaEntity createFromParcel(Parcel source) {return new VideoMediaEntity(source);}

      @Override
      public VideoMediaEntity[] newArray(int size) {return new VideoMediaEntity[size];}
   };
}
