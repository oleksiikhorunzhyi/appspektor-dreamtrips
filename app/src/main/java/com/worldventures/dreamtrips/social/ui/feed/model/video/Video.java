package com.worldventures.dreamtrips.social.ui.feed.model.video;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.social.ui.feed.model.BaseFeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.comment.Comment;

import java.util.Date;
import java.util.List;

public class Video extends BaseFeedEntity implements Parcelable {

   private String uploadId;
   private String thumbnail;
   private double aspectRatio;
   private Date createdAt;
   private long duration;
   private List<Quality> qualities;

   public Video() {
      //do nothing
   }

   public String getThumbnail() {
      return thumbnail;
   }

   public List<Quality> getQualities() {
      return qualities;
   }

   public void setQualities(List<Quality> qualities) {
      this.qualities = qualities;
   }

   public double getAspectRatio() {
      return aspectRatio;
   }

   public long getDuration() {
      return duration;
   }

   @Override
   public Date getCreatedAt() {
      return createdAt;
   }

   public String getUploadId() {
      return uploadId;
   }

   public void setUploadId(String uploadId) {
      this.uploadId = uploadId;
   }

   public void setDuration(long duration) {
      this.duration = duration;
   }

   public void setThumbnail(String thumbnail) {
      this.thumbnail = thumbnail;
   }


   public void setAspectRatio(double aspectRatio) {
      this.aspectRatio = aspectRatio;
   }

   public void setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
   }

   @Override
   public String place() {
      return null;
   }

   protected Video(Parcel in) {
      this.uploadId = in.readString();
      this.thumbnail = in.readString();
      this.aspectRatio = in.readDouble();
      this.duration = in.readLong();
      this.qualities = in.createTypedArrayList(Quality.CREATOR);
      this.uid = in.readString();
      this.owner = in.readParcelable(User.class.getClassLoader());
      this.commentsCount = in.readInt();
      this.comments = in.createTypedArrayList(Comment.CREATOR);
      this.liked = in.readByte() != 0;
      this.likesCount = in.readInt();
      this.language = in.readString();
      this.firstLikerName = in.readString();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.uploadId);
      dest.writeString(this.thumbnail);
      dest.writeDouble(this.aspectRatio);
      dest.writeLong(this.duration);
      dest.writeTypedList(this.qualities);
      dest.writeString(this.uid);
      dest.writeParcelable(this.owner, flags);
      dest.writeInt(this.commentsCount);
      dest.writeTypedList(this.comments);
      dest.writeByte(this.liked ? (byte) 1 : (byte) 0);
      dest.writeInt(this.likesCount);
      dest.writeString(this.language);
      dest.writeString(this.firstLikerName);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   public static final Creator<Video> CREATOR = new Creator<Video>() {
      @Override
      public Video createFromParcel(Parcel in) {
         return new Video(in);
      }

      @Override
      public Video[] newArray(int size) {
         return new Video[size];
      }
   };

}
