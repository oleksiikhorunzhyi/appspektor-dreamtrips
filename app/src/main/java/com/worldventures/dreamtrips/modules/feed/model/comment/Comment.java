package com.worldventures.dreamtrips.modules.feed.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.Date;

public class Comment extends BaseEntity implements Parcelable {

    int object_id;
    int parent_id;
    String text;
    User user;
    @SerializedName("created_at")
    Date createdAt;
    @SerializedName("updated_at")
    Date updatedAt;
    boolean update;


    protected Comment(Parcel in) {
        id = in.readInt();
        object_id = in.readInt();
        parent_id = in.readInt();
        text = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public boolean isUpdate() {
        return update;
    }

    public String getMessage() {
        return text;
    }

    public void setMessage(String text) {
        this.text = text;
    }

    public User getOwner() {
        return user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(object_id);
        parcel.writeInt(parent_id);
        parcel.writeString(text);
        parcel.writeParcelable(user, i);
    }
}
