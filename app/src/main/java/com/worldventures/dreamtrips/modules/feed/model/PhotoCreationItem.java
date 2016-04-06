package com.worldventures.dreamtrips.modules.feed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PhotoCreationItem implements Parcelable {

    long id;
    @NotNull
    String imageUrl;
    @NotNull
    String filePath;
    @NotNull
    String location;
    @NotNull
    UploadTask.Status status;
    @NotNull
    ArrayList<PhotoTag> basePhotoTags = new ArrayList<>();
    @NotNull
    ArrayList<PhotoTag> cachedAddedPhotoTags = new ArrayList<>();
    @NotNull
    ArrayList<PhotoTag> cachedRemovedPhotoTags = new ArrayList<>();
    @NotNull
    String mediaAttachmentType;

    public PhotoCreationItem() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<PhotoTag> getBasePhotoTags() {
        return basePhotoTags;
    }

    public void setBasePhotoTags(ArrayList<PhotoTag> basePhotoTags) {
        this.basePhotoTags = basePhotoTags;
    }

    @NotNull
    public ArrayList<PhotoTag> getCachedAddedPhotoTags() {
        return cachedAddedPhotoTags;
    }

    @NotNull
    public ArrayList<PhotoTag> getCachedRemovedPhotoTags() {
        return cachedRemovedPhotoTags;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NotNull
    public UploadTask.Status getStatus() {
        return status;
    }

    public List<PhotoTag> getCombinedTags() {
        List<PhotoTag> combinedTags = new ArrayList<>(cachedAddedPhotoTags);
        combinedTags.removeAll(cachedRemovedPhotoTags);
        combinedTags.addAll(basePhotoTags);
        combinedTags.removeAll(cachedRemovedPhotoTags);

        return combinedTags;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageUrl);
        dest.writeString(this.filePath);
        dest.writeString(this.location);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeTypedList(basePhotoTags);
        dest.writeTypedList(cachedAddedPhotoTags);
        dest.writeTypedList(cachedRemovedPhotoTags);
    }

    protected PhotoCreationItem(Parcel in) {
        this.imageUrl = in.readString();
        this.filePath = in.readString();
        this.location = in.readString();
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : UploadTask.Status.values()[tmpStatus];
        this.basePhotoTags = in.createTypedArrayList(PhotoTag.CREATOR);
        this.cachedAddedPhotoTags = in.createTypedArrayList(PhotoTag.CREATOR);
        this.cachedRemovedPhotoTags = in.createTypedArrayList(PhotoTag.CREATOR);
    }

    public static final Creator<PhotoCreationItem> CREATOR = new Creator<PhotoCreationItem>() {
        @Override
        public PhotoCreationItem createFromParcel(Parcel source) {
            return new PhotoCreationItem(source);
        }

        @Override
        public PhotoCreationItem[] newArray(int size) {
            return new PhotoCreationItem[size];
        }
    };

    public void setStatus(@NotNull UploadTask.Status status) {
        this.status = status;
    }

    public void setMediaAttachmentType(String mediaAttachmentType) {
        this.mediaAttachmentType = mediaAttachmentType;
    }

    public UploadTask toUploadTask() {
        UploadTask uploadTask = new UploadTask();
        uploadTask.setId(getId());
        uploadTask.setFilePath(getFilePath());
        uploadTask.setLocationName(getLocation());
        uploadTask.setStatus(getStatus());
        uploadTask.setType(mediaAttachmentType);
        return uploadTask;
    }
}
