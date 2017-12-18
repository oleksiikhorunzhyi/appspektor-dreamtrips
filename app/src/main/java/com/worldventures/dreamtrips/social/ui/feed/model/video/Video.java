package com.worldventures.dreamtrips.social.ui.feed.model.video;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.social.ui.feed.model.BaseFeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.comment.Comment;

import java.util.Date;

public class Video extends BaseFeedEntity implements Parcelable {

   private String uploadId;
   private String thumbnail;
   private String sdUrl;
   private String hdUrl;
   private double aspectRatio;
   private Date createdAt;
   private long duration;

   public Video() {
      //do nothing
   }

   public String getUploadId() {
      return uploadId;
   }

   public String getThumbnail() {
      return thumbnail;
   }

   public String getSdUrl() {
      return sdUrl;
   }

   public String getHdUrl() {
      return hdUrl;
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

   public void setUploadId(String uploadId) {
      this.uploadId = uploadId;
   }

   public void setDuration(long duration) {
      this.duration = duration;
   }

   public void setThumbnail(String thumbnail) {
      this.thumbnail = thumbnail;
   }

   public void setSdUrl(String sdUrl) {
      this.sdUrl = sdUrl;
   }

   public void setHdUrl(String hdUrl) {
      this.hdUrl = hdUrl;
   }

   public void setAspectRatio(double aspectRatio) {
      this.aspectRatio = aspectRatio;
   }

   public void setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.uploadId);
      dest.writeString(this.thumbnail);
      dest.writeString(this.sdUrl);
      dest.writeString(this.hdUrl);
      dest.writeDouble(this.aspectRatio);
      dest.writeString(this.uid);
      dest.writeParcelable(this.owner, flags);
      dest.writeInt(this.commentsCount);
      dest.writeTypedList(this.comments);
      dest.writeByte(this.liked ? (byte) 1 : (byte) 0);
      dest.writeInt(this.likesCount);
      dest.writeString(this.language);
      dest.writeString(this.firstLikerName);
   }

   protected Video(Parcel in) {
      this.uploadId = in.readString();
      this.thumbnail = in.readString();
      this.sdUrl = in.readString();
      this.hdUrl = in.readString();
      this.aspectRatio = in.readDouble();
      this.uid = in.readString();
      this.owner = in.readParcelable(User.class.getClassLoader());
      this.commentsCount = in.readInt();
      this.comments = in.createTypedArrayList(Comment.CREATOR);
      this.liked = in.readByte() != 0;
      this.likesCount = in.readInt();
      this.language = in.readString();
      this.firstLikerName = in.readString();
   }

   public static final Creator<Video> CREATOR = new Creator<Video>() {
      @Override
      public Video createFromParcel(Parcel source) {
         return new Video(source);
      }

      @Override
      public Video[] newArray(int size) {
         return new Video[size];
      }
   };

   @Override
   public String place() {
      return null;
   }
}
