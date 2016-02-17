package com.messenger.entities;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

public class PhotoAttachment implements IFullScreenObject {

    private final User user;
    private final Image image;

    public PhotoAttachment(Image photo, User user) {
        this.image = photo;
        this.user = user;
    }

    public PhotoAttachment(Parcel source) {
        this.image = source.readParcelable(Image.class.getClassLoader());
        this.user = source.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<PhotoAttachment> CREATOR = new Creator<PhotoAttachment>() {
        public PhotoAttachment createFromParcel(Parcel source) {
            return new PhotoAttachment(source);
        }

        public PhotoAttachment[] newArray(int size) {
            return new PhotoAttachment[size];
        }
    };

    @Override
    public Image getFSImage() {
        return image;
    }

    @Override
    public String getFSTitle() {
        return null;
    }

    @Override
    public String getFSDescription() {
        return null;
    }

    @Override
    public String getFSShareText() {
        return null;
    }

    @Override
    public String getFSId() {
        return image.getUrl();
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
        return user.getAvatar().getOriginal();
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getImagePath() {
        return image.getUrl();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(image, flags);
        dest.writeParcelable(user, flags);
    }
}
