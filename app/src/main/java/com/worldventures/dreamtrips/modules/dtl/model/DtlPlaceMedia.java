package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DtlPlaceMedia implements Parcelable {

    private int mediaId;
    private String mediaFileName;

    public DtlPlaceMedia() {
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
        dest.writeInt(mediaId);
        dest.writeString(mediaFileName);
    }
}
