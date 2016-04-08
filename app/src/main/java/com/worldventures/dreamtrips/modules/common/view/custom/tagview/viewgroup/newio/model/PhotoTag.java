package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.innahema.collections.query.queriables.Queryable;

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

public class PhotoTag implements Parcelable, Serializable, Cloneable {

    private int targetUserId;
    private TagPosition position;
    private String title;

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
        result.title = photoTag.title;
        return result;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getTargetUserId() {
        return targetUserId;
    }

    public static boolean isIntersectedWithPhotoTags(List<PhotoTag> combinedTags, PhotoTag suggestion) {
        return Queryable.from(combinedTags)
                .any(element -> element.getProportionalPosition().intersected(suggestion.getProportionalPosition()));
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhotoTag photoTag = (PhotoTag) o;

        if (targetUserId != photoTag.targetUserId) return false;
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
        dest.writeString(this.title);
    }

    protected PhotoTag(Parcel in) {
        this.targetUserId = in.readInt();
        this.position = in.readParcelable(TagPosition.class.getClassLoader());
        this.title = in.readString();
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
