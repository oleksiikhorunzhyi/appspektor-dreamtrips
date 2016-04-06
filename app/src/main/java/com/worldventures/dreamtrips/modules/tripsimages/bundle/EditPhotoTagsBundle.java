package com.worldventures.dreamtrips.modules.tripsimages.bundle;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.List;

public class EditPhotoTagsBundle implements Parcelable {

    private PhotoEntity photo;
    private List<PhotoTag> photoTags;
    private long requestId;

    public EditPhotoTagsBundle(long requestId, PhotoEntity photo, List<PhotoTag> photoTags) {
        this.requestId = requestId;
        this.photo = photo;
        this.photoTags = photoTags;
    }

    public EditPhotoTagsBundle(Parcel in) {
    }

    public static final Creator<EditPhotoTagsBundle> CREATOR = new Creator<EditPhotoTagsBundle>() {
        @Override
        public EditPhotoTagsBundle createFromParcel(Parcel in) {
            return new EditPhotoTagsBundle(in);
        }

        @Override
        public EditPhotoTagsBundle[] newArray(int size) {
            return new EditPhotoTagsBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public PhotoEntity getPhoto() {
        return photo;
    }

    public List<PhotoTag> getPhotoTags() {
        return photoTags;
    }


    public long getRequestId() {
        return requestId;
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

}
