package com.worldventures.dreamtrips.modules.feed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionState;

import static io.techery.janet.ActionState.Status;

public class PhotoCreationItem implements Parcelable {

    long id;
    @NotNull
    String filePath;
    @NotNull
    String originUrl = "";
    @NotNull
    String location;
    @NotNull
    Status status;
    @NotNull
    ArrayList<PhotoTag> basePhotoTags = new ArrayList<>();
    @NotNull
    ArrayList<PhotoTag> cachedAddedPhotoTags = new ArrayList<>();
    @NotNull
    ArrayList<PhotoTag> cachedRemovedPhotoTags = new ArrayList<>();
    @NotNull
    String mediaAttachmentType;
    String title;

    List<PhotoTag> suggestions = new ArrayList<>();
    private int width;
    private int height;

    public PhotoCreationItem() {
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @NotNull
    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(@NotNull String originUrl) {
        this.originUrl = originUrl;
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

    public List<PhotoTag> getSuggestions() {
        return suggestions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NotNull
    public Status getStatus() {
        return status;
    }

    public List<PhotoTag> getCombinedTags() {
        List<PhotoTag> combinedTags = new ArrayList<>(cachedAddedPhotoTags);
        combinedTags.removeAll(cachedRemovedPhotoTags);
        combinedTags.addAll(basePhotoTags);
        combinedTags.removeAll(cachedRemovedPhotoTags);

        return combinedTags;
    }

    @NotNull
    public String getTitle() {
        return title != null ? title : "";
    }

    public void setSuggestions(List<PhotoTag> suggestions) {
        this.suggestions = suggestions;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.filePath);
        dest.writeString(this.originUrl);
        dest.writeString(this.location);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeTypedList(basePhotoTags);
        dest.writeTypedList(cachedAddedPhotoTags);
        dest.writeTypedList(cachedRemovedPhotoTags);
        dest.writeString(this.mediaAttachmentType);
        dest.writeTypedList(suggestions);
        dest.writeString(title);
    }

    protected PhotoCreationItem(Parcel in) {
        this.id = in.readLong();
        this.filePath = in.readString();
        this.originUrl = in.readString();
        this.location = in.readString();
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : ActionState.Status.values()[tmpStatus];
        this.basePhotoTags = in.createTypedArrayList(PhotoTag.CREATOR);
        this.cachedAddedPhotoTags = in.createTypedArrayList(PhotoTag.CREATOR);
        this.cachedRemovedPhotoTags = in.createTypedArrayList(PhotoTag.CREATOR);
        this.mediaAttachmentType = in.readString();
        this.suggestions = in.createTypedArrayList(PhotoTag.CREATOR);
        this.title = in.readString();
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

    public void setStatus(@NotNull Status status) {
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
        uploadTask.setOriginUrl(getOriginUrl());
        uploadTask.setType(mediaAttachmentType);
        uploadTask.setTitle(title);
        return uploadTask;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
