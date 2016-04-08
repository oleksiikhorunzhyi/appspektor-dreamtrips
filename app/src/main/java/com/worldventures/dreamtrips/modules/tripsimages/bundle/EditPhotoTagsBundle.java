package com.worldventures.dreamtrips.modules.tripsimages.bundle;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EditPhotoTagsBundle implements Parcelable {

    private PhotoEntity photo;
    private List<PhotoTag> photoTags;
    private List<PhotoTag> suggestions;
    private PhotoTag activeSuggestion;
    private long requestId;

    public EditPhotoTagsBundle() {
    }

    public void setPhoto(PhotoEntity photo) {
        this.photo = photo;
    }

    public void setPhotoTags(List<PhotoTag> photoTags) {
        this.photoTags = photoTags;
    }

    public List<PhotoTag> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<PhotoTag> suggestions) {
        this.suggestions = suggestions;
    }

    public void setActiveSuggestion(PhotoTag activeSuggestion) {
        this.activeSuggestion = activeSuggestion;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public PhotoEntity getPhoto() {
        return photo;
    }

    @NotNull
    public List<PhotoTag> getPhotoTags() {
        if (photoTags == null) {
            photoTags = new ArrayList<>();
        }
        return photoTags;
    }


    public long getRequestId() {
        return requestId;
    }

    public PhotoTag getActiveSuggestion() {
        return activeSuggestion;
    }

    public static class PhotoEntity implements Parcelable {
        String url;
        String filePath;

        public PhotoEntity(String url, String filePath) {
            this.url = url;
            this.filePath = filePath;
        }

        public Uri getImageUri() {
            return Uri.parse(TextUtils.isEmpty(filePath) ? url : filePath);
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.url);
            dest.writeString(this.filePath);
        }

        protected PhotoEntity(Parcel in) {
            this.url = in.readString();
            this.filePath = in.readString();
        }

        public static final Creator<PhotoEntity> CREATOR = new Creator<PhotoEntity>() {
            public PhotoEntity createFromParcel(Parcel source) {
                return new PhotoEntity(source);
            }

            public PhotoEntity[] newArray(int size) {
                return new PhotoEntity[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.photo, flags);
        dest.writeTypedList(photoTags);
        dest.writeParcelable(this.activeSuggestion, flags);
        dest.writeLong(this.requestId);
    }

    public EditPhotoTagsBundle(Parcel in) {
        this.photo = in.readParcelable(PhotoEntity.class.getClassLoader());
        this.photoTags = in.createTypedArrayList(PhotoTag.CREATOR);
        this.activeSuggestion = in.readParcelable(PhotoTag.class.getClassLoader());
        this.requestId = in.readLong();
    }

    public static final Creator<EditPhotoTagsBundle> CREATOR = new Creator<EditPhotoTagsBundle>() {
        @Override
        public EditPhotoTagsBundle createFromParcel(Parcel source) {
            return new EditPhotoTagsBundle(source);
        }

        @Override
        public EditPhotoTagsBundle[] newArray(int size) {
            return new EditPhotoTagsBundle[size];
        }
    };
}
