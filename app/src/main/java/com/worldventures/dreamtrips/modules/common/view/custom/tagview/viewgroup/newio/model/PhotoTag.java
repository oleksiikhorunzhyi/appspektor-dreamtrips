package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

public class PhotoTag implements Parcelable, Serializable, Cloneable {

    private int targetUserId;
    private TagPosition position;
    private User user;

    /**
     * For serialization
     */
    public PhotoTag() {
    }

    public PhotoTag(TagPosition position, int targetUserId) {
        this.position = position;
        this.targetUserId = targetUserId;
    }

    public TagPosition getProportionalPosition() {
        return position;
    }

    public void setTagPosition(TagPosition position) {
        this.position = position;
    }

    public static PhotoTag cloneTag(PhotoTag photoTag) {
        PhotoTag result = new PhotoTag();
        result.targetUserId = photoTag.targetUserId;
        result.position = photoTag.position;
        result.user = photoTag.user;
        return result;
    }

    public void setUser(User user) {
        this.user = user;

        if (user != null) {
            targetUserId = user.getId();
        }
    }

    public String getTitle() {
        return user.getFullName();
    }

    public int getTargetUserId() {
        return targetUserId;
    }

    public static boolean isIntersectedWithPhotoTags(List<PhotoTag> combinedTags, PhotoTag suggestion) {
        return Queryable.from(combinedTags)
                .any(element -> element.getProportionalPosition().intersected(suggestion.getProportionalPosition()));
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhotoTag photoTag = (PhotoTag) o;

        if (targetUserId != photoTag.targetUserId &&
                (user == null || photoTag.getUser() == null || user.getId() != photoTag.getUser().getId()))
            return false;
        Timber.v("-------------");
        Timber.v(photoTag.getProportionalPosition().toString());
        Timber.v(position.toString());
        Timber.v("-------------");
        return position != null ? position.equals(photoTag.position) : photoTag.position == null;

    }

    @Override
    public int hashCode() {
        int result = targetUserId;
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.targetUserId);
        dest.writeParcelable(this.position, flags);
        dest.writeParcelable(this.user, flags);
    }

    protected PhotoTag(Parcel in) {
        this.targetUserId = in.readInt();
        this.position = in.readParcelable(TagPosition.class.getClassLoader());
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<PhotoTag> CREATOR = new Creator<PhotoTag>() {
        @Override
        public PhotoTag createFromParcel(Parcel source) {
            return new PhotoTag(source);
        }

        @Override
        public PhotoTag[] newArray(int size) {
            return new PhotoTag[size];
        }
    };
}
