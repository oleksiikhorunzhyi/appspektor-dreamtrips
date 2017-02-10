package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Review;
import java.util.ArrayList;
import java.util.List;

public class ReviewObject implements Parcelable {

   private String urlImageUser;
   private String nameUser;
   private float ratingCommentUser;
   private String timeWrote;
   private String comment;

   public ReviewObject(String urlImageUser, String nameUser, float ratingCommentUser, String timeWrote, String comment) {
      this.urlImageUser = urlImageUser;
      this.nameUser = nameUser;
      this.ratingCommentUser = ratingCommentUser;
      this.timeWrote = timeWrote;
      this.comment = comment;
   }

   public String getUrlImageUser() {
      return urlImageUser;
   }

   public void setUrlImageUser(String urlImageUser) {
      this.urlImageUser = urlImageUser;
   }

   public String getNameUser() {
      return nameUser;
   }

   public void setNameUser(String nameUser) {
      this.nameUser = nameUser;
   }

   public String getTimeWrote() {
      return timeWrote;
   }

   public void setTimeWrote(String timeWrote) {
      this.timeWrote = timeWrote;
   }

   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public float getRatingCommentUser() {
      return ratingCommentUser;
   }

   public void setRatingCommentUser(float ratingCommentUser) {
      this.ratingCommentUser = ratingCommentUser;
   }

   private static ReviewObject getObject(Review review) {
      return new ReviewObject("null",
            review.getUserNickName(),
            review.getRating(),
            review.getLastModeratedTimeUtc(),
            review.getReviewText());
   }

   public static ArrayList<ReviewObject> getReviewList(List<Review> reviewList) {
      ArrayList<ReviewObject> reviewObjectList = new ArrayList<>();
      for (Review r : reviewList) {
         reviewObjectList.add(getObject(r));
      }
      return reviewObjectList;
   }

   public static ArrayList<ReviewObject> getReviewListLimit(List<Review> reviewList) {
      ArrayList<ReviewObject> reviewObjectList = new ArrayList<>();
      for (int i = 0; i < 2; i++) {
         reviewObjectList.add(getObject(reviewList.get(i)));
      }
      return reviewObjectList;
   }


   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.urlImageUser);
      dest.writeString(this.nameUser);
      dest.writeFloat(this.ratingCommentUser);
      dest.writeString(this.timeWrote);
      dest.writeString(this.comment);
   }

   protected ReviewObject(Parcel in) {
      this.urlImageUser = in.readString();
      this.nameUser = in.readString();
      this.ratingCommentUser = in.readFloat();
      this.timeWrote = in.readString();
      this.comment = in.readString();
   }

   public static final Parcelable.Creator<ReviewObject> CREATOR = new Parcelable.Creator<ReviewObject>() {
      @Override
      public ReviewObject createFromParcel(Parcel source) {return new ReviewObject(source);}

      @Override
      public ReviewObject[] newArray(int size) {return new ReviewObject[size];}
   };
}
