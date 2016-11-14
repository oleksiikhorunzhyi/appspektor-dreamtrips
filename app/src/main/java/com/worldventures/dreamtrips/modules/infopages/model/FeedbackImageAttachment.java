package com.worldventures.dreamtrips.modules.infopages.model;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import java.util.UUID;

public class FeedbackImageAttachment implements FeedbackAttachment, IFullScreenObject {

   private String id;
   /*
    * Path of the picture that user selected from photo picker
    */
   private String originalFilePath;
   private String url;
   private Type type;

   public FeedbackImageAttachment() {
      id = UUID.randomUUID().toString();
      type = Type.IMAGE;
   }

   public FeedbackImageAttachment(String originalFilePath) {
      this();
      this.originalFilePath = originalFilePath;
   }

   public String getId() {
      return id;
   }

   @Override
   public Type getType() {
      return type;
   }

   public String getOriginalFilePath() {
      return originalFilePath;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   @Override
   public String getUrl() {
      return url;
   }

   @Override
   public Image getFSImage() {
      Image image = new Image();
      image.setUrl(url);
      return image;
   }

   @Override
   public String getFSTitle() {
      return "";
   }

   @Override
   public String getFSDescription() {
      return "";
   }

   @Override
   public String getFSShareText() {
      return "";
   }

   @Override
   public String getFSId() {
      return "";
   }

   @Override
   public int getFSCommentCount() {
      return 0;
   }

   @Override
   public int getFSLikeCount() {
      return 0;
   }

   @Override
   public String getFSLocation() {
      return null;
   }

   @Override
   public String getFSDate() {
      return null;
   }

   @Override
   public String getFSUserPhoto() {
      return null;
   }

   @Override
   public User getUser() {
      return null;
   }

   @Override
   public String getImagePath() {
      return url;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      FeedbackImageAttachment that = (FeedbackImageAttachment) o;

      return id != null ? id.equals(that.id) : that.id == null;
   }

   @Override
   public int hashCode() {
      return id != null ? id.hashCode() : 0;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.id);
      dest.writeString(this.originalFilePath);
      dest.writeString(this.url);
      dest.writeInt(this.type == null ? -1 : this.type.ordinal());
   }

   protected FeedbackImageAttachment(Parcel in) {
      this.id = in.readString();
      this.originalFilePath = in.readString();
      this.url = in.readString();
      int tmpType = in.readInt();
      this.type = tmpType == -1 ? null : Type.values()[tmpType];
   }

   public static final Creator<FeedbackImageAttachment> CREATOR = new Creator<FeedbackImageAttachment>() {
      @Override
      public FeedbackImageAttachment createFromParcel(Parcel source) {return new FeedbackImageAttachment(source);}

      @Override
      public FeedbackImageAttachment[] newArray(int size) {return new FeedbackImageAttachment[size];}
   };
}
