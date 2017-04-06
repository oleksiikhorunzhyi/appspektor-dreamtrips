package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Review;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewImages;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.UserImage;
import java.util.ArrayList;
import java.util.List;

public class ReviewObject implements Parcelable {

   private String reviewId;
   private String urlImageUser;
   private String nameUser;
   private float ratingCommentUser;
   private String timeWrote;
   private String comment;
   private boolean isVerifiedReview;
   private List<ReviewImages> urlReviewImages;

   public ReviewObject(String reviewId, String urlImageUser, String nameUser, float ratingCommentUser, String timeWrote, String comment, boolean isVerifiedReview, List<ReviewImages> urlReviewImages) {
      this.reviewId = reviewId;
      this.urlImageUser = urlImageUser;
      this.nameUser = nameUser;
      this.ratingCommentUser = ratingCommentUser;
      this.timeWrote = timeWrote;
      this.comment = comment;
      this.isVerifiedReview = isVerifiedReview;
      this.urlReviewImages = urlReviewImages;
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

   public float getRatingCommentUser() {
      return ratingCommentUser;
   }

   public void setRatingCommentUser(float ratingCommentUser) {
      this.ratingCommentUser = ratingCommentUser;
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

   public boolean isVerifiedReview() {
      return isVerifiedReview;
   }

   public void setVerifiedReview(boolean verifiedReview) {
      isVerifiedReview = verifiedReview;
   }

   public String getReviewId() {
      return reviewId;
   }

   public List<ReviewImages> getUrlReviewImages() {
      return urlReviewImages;
   }

   public void setUrlReviewImages(List<ReviewImages> arrayList) {
      this.urlReviewImages = arrayList;
   }

   private static ReviewObject getObject(Review review) {
      return new ReviewObject(review.reviewId(),
            getUrlImageUser(review.userImage()),
            review.userNickName(),
            review.rating(),
            review.lastModeratedTimeUtc(),
            review.reviewText(),
            review.verified(),
            review.reviewImagesList());
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
      dest.writeByte(this.isVerifiedReview ? (byte) 1 : (byte) 0);
      dest.writeList(this.urlReviewImages);
   }

   protected ReviewObject(Parcel in) {
      this.urlImageUser = in.readString();
      this.nameUser = in.readString();
      this.ratingCommentUser = in.readFloat();
      this.timeWrote = in.readString();
      this.comment = in.readString();
      this.isVerifiedReview = in.readByte() != 0;
      this.urlReviewImages = in.readParcelable(ReviewImages.class.getClassLoader());
   }

   protected static String getUrlImageUser(UserImage userImage) {
      String urlImage = null;
      if (userImage != null) {
         urlImage = userImage.thumb();
      }
      return urlImage;
   }

   public static final Creator<ReviewObject> CREATOR = new Creator<ReviewObject>() {
      @Override
      public ReviewObject createFromParcel(Parcel source) {return new ReviewObject(source);}

      @Override
      public ReviewObject[] newArray(int size) {return new ReviewObject[size];}
   };
}
