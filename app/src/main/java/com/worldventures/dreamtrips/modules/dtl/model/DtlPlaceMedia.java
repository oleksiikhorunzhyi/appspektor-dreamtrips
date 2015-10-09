package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DtlPlaceMedia implements Parcelable {

    private boolean isDefault;
    private int mediaId;
    private String mediaFileName;

    public DtlPlaceMedia() {
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaFileName() {
        return mediaFileName;
    }

    public void setMediaFileName(String mediaFileName) {
        this.mediaFileName = mediaFileName;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected DtlPlaceMedia(Parcel in) {
        isDefault = in.readByte() == 1;
        mediaId = in.readInt();
        mediaFileName = in.readString();
    }

    public static final Creator<DtlPlaceMedia> CREATOR = new Creator<DtlPlaceMedia>() {
        @Override
        public DtlPlaceMedia createFromParcel(Parcel in) {
            return new DtlPlaceMedia(in);
        }

        @Override
        public DtlPlaceMedia[] newArray(int size) {
            return new DtlPlaceMedia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isDefault ? 1 : 0));
        dest.writeInt(mediaId);
        dest.writeString(mediaFileName);
    }
}
