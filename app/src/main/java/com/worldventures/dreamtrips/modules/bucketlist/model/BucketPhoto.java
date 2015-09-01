package com.worldventures.dreamtrips.modules.bucketlist.model;

import android.os.Parcel;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BucketPhoto implements Serializable, IFullScreenObject, android.os.Parcelable {

    public static final long serialVersionUID = 14534647;

    public static final Creator<BucketPhoto> CREATOR = new Creator<BucketPhoto>() {
        public BucketPhoto createFromParcel(Parcel source) {
            return new BucketPhoto(source);
        }

        public BucketPhoto[] newArray(int size) {
            return new BucketPhoto[size];
        }
    };

    private String uid;
    @SerializedName("origin_url")
    private String originUrl;
    private String url;

    private boolean isCover;

    public BucketPhoto() {
    }

    private BucketPhoto(Parcel in) {
        this.originUrl = in.readString();
        this.url = in.readString();
        this.uid = in.readString();
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public void setIsCover(boolean isCover) {
        this.isCover = isCover;
    }

    public boolean isCover() {
        return isCover;
    }

    @Override
    public String getFsId() {
        return uid;
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
    public String getFsDescription() {
        return "";
    }

    @Override
    public String getFsShareText() {
        return "";
    }

    @Override
    public int getFsCommentCount() {
        return -1;
    }

    @Override
    public int getFsLikeCount() {
        return -1;
    }

    @Override
    public String getFsLocation() {
        return "";
    }

    @Override
    public String getFsDate() {
        return "";
    }

    @Override
    public String getFsUserPhoto() {
        return "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public User getUser() {
        return null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.originUrl);
        dest.writeString(this.url);
        dest.writeString(this.uid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BucketPhoto that = (BucketPhoto) o;

        return !(uid != null ? !uid.equals(that.uid) : that.uid != null);

    }

    @Override
    public int hashCode() {
        return uid != null ? uid.hashCode() : 0;
    }
}
