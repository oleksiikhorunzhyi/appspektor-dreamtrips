
package com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReviewsMerchant {

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("ratingAvarage")
    @Expose
    private Double ratingAvarage;
    @SerializedName("reviews")
    @Expose
    private List<Review> reviews = null;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Double getRatingAvarage() {
        return ratingAvarage;
    }

    public void setRatingAvarage(Double ratingAvarage) {
        this.ratingAvarage = ratingAvarage;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

}
