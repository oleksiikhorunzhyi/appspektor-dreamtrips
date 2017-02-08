
package com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("lastModeratedTimeUtc")
    @Expose
    private String lastModeratedTimeUtc;
    @SerializedName("reviewId")
    @Expose
    private String reviewId;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("userNickName")
    @Expose
    private String userNickName;
    @SerializedName("userImage")
    @Expose
    private String userImage;
    @SerializedName("reviewText")
    @Expose
    private String reviewText;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("verified")
    @Expose
    private Boolean verified;

    public String getLastModeratedTimeUtc() {
        return lastModeratedTimeUtc;
    }

    public void setLastModeratedTimeUtc(String lastModeratedTimeUtc) {
        this.lastModeratedTimeUtc = lastModeratedTimeUtc;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

}
