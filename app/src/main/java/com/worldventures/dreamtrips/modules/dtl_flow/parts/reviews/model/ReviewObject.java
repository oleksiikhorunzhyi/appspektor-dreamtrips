package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Review;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReviewObject implements Parcelable {

    private String urlImageUser;
    private String nameUser;
    private float ratingCommentUser;
    private String timeWrote;
    private String comment;

    public ReviewObject() {
    }

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

    public static ArrayList<ReviewObject> getDummies(int size){
        ArrayList<ReviewObject> list = new ArrayList<ReviewObject>();
        DecimalFormat df = new DecimalFormat("#.#");
        
        for (int i=0; i<size; i++){
            float value = 0;
            if (i<2){
                value = (float) 1.3;
            } else if (i<4){
                value = (float) 1.6;
            } else if (i<6){
                value = (float) 1.8;
            } else if (i<8){
                value = (float) 2.1;
            } else if (i<10){
                value = (float) 2.4;
            } else if (i<12){
                value = (float) 2.6;
            } else if (i<14){
                value = (float) 2.8;
            } else if (i<16){
                value = (float) 3.2;
            } else if (i<18){
                value = (float) 3.4;
            } else if (i<20){
                value = (float) 3.6;
            } else if (i<22){
                value = (float) 3.8;
            } else if (i<24){
                value = (float) 4.0;
            } else {
                value = (float) 4.4;
            }
            list.add(getObject(value, i));
        }
        return list;
    }

    private static ReviewObject getObject(float rating, int pos){
        ReviewObject object = null;
        if (pos == 0){
            object = new ReviewObject("null",
                  "Wade Wilson",
                  4,
                  "2016-02-08T20:20:11.13Z UTC",
                  "Lorem Ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis...");
        } else if (pos == 1){
            object = new ReviewObject("null",
                  "Diana Prince",
                  4,
                  "2017-01-08T20:00:11.123Z UTC",
                  "Lorem Ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis...");
        } else if(pos == 2){
            object = new ReviewObject("http://rs656.pbsrc.com/albums/uu287/WouaipHOULA/DrHouse.jpg~c200",
                  "Kenneth Rivera",
                  rating,
                  "2017-01-06T20:00:11.123Z UTC",
                  "Lorem Ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis...");
        } else if(pos == 3){
            object = new ReviewObject("http://rs656.pbsrc.com/albums/uu287/WouaipHOULA/DrHouse.jpg~c200",
                  "Kenneth Rivera",
                  rating,
                  "2017-01-06T20:02:11.123Z UTC",
                  "Lorem Ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis...");
        } else if(pos == 4){
            object = new ReviewObject("http://rs656.pbsrc.com/albums/uu287/WouaipHOULA/DrHouse.jpg~c200",
                  "Kenneth Rivera",
                  rating,
                  "2017-01-06T20:00:31.123Z UTC",
                  "Lorem Ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis...");
        } else {
            object = new ReviewObject("http://rs656.pbsrc.com/albums/uu287/WouaipHOULA/DrHouse.jpg~c200",
                  "Kenneth Rivera",
                  rating,
                  "2017-01-06T20:00:11.023Z UTC",
                  "Lorem Ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis...");
        }
        return object;
    }

    private static ReviewObject getObject(Review review){
        return new ReviewObject("null",
              review.getUserNickName(),
              review.getRating(),
              review.getLastModeratedTimeUtc(),
              review.getReviewText());
    }

    public static ArrayList<ReviewObject> getReviewList(List<Review> reviewList){
        ArrayList<ReviewObject> reviewObjectList = new ArrayList<>();
        for (Review r : reviewList) {
            reviewObjectList.add(getObject(r));
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
