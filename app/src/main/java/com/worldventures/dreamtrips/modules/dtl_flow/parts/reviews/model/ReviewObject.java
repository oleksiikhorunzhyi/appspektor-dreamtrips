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
            list.add(getObject(value));
        }
        return list;
    }

    private static ReviewObject getObject(float rating){
        return new ReviewObject("http://rs656.pbsrc.com/albums/uu287/WouaipHOULA/DrHouse.jpg~c200",
                                "Kenneth Rivera",
                                rating,
                                "3 min ago",
                                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.");
    }

    private static ReviewObject getObject(Review review){
        return new ReviewObject("http://rs656.pbsrc.com/albums/uu287/WouaipHOULA/DrHouse.jpg~c200",
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
