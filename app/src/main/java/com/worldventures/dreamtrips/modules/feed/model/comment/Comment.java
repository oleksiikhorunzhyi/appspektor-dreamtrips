package com.worldventures.dreamtrips.modules.feed.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.TranslatableItem;
import com.worldventures.dreamtrips.modules.feed.model.UidItem;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Parcelable, Serializable, UidItem, TranslatableItem {

    String uid;
    String parent_id;
    /**
     * Parent entity uid
     */
    String originId;
    String text;
    User user;
    @SerializedName("created_at")
    Date createdAt;
    @SerializedName("updated_at")
    Date updatedAt;
    boolean update;
    String language;
    String translation;
    boolean translated;

    public Comment() {
    }

    protected Comment(Parcel in) {
        uid = in.readString();
        parent_id = in.readString();
        text = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        language = in.readString();
        translation = in.readString();
        translated = in.readByte() == 1;
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
    public String getUid() {
        return uid;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(parent_id);
        parcel.writeString(text);
        parcel.writeParcelable(user, i);
        parcel.writeString(language);
        parcel.writeString(translation);
        parcel.writeByte((byte) (translated ? 1 : 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        return !(uid != null ? !uid.equals(comment.uid) : comment.uid != null);

    }

    @Override
    public int hashCode() {
        return uid != null ? uid.hashCode() : 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Translation
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public String getTranslation() {
        return translation;
    }

    @Override
    public void setTranslation(String translation) {
        this.translation = translation;
    }

    @Override
    public String getOriginalText() {
        return text;
    }

    public String getLanguageFrom() {
        return language;
    }

    @Override
    public boolean isTranslated() {
        return translated;
    }

    @Override
    public void setTranslated(boolean translated) {
        this.translated = translated;
    }
}
