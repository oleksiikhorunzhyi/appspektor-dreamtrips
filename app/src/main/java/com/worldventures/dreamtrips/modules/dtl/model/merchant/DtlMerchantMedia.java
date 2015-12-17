package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.core.ui.fragment.ImagePathHolder;

public class DtlMerchantMedia implements Parcelable, ImagePathHolder {

    private String imageId;
    private String url;
    private String logoUrl;
    private String name;
    private String description;
    private String logo;

    public DtlMerchantMedia() {
    }

    @Override
    public String getImagePath() {
        return url;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////
    protected DtlMerchantMedia(Parcel in) {
        imageId = in.readString();
        url = in.readString();
        logoUrl = in.readString();
        name = in.readString();
        description = in.readString();
        logo = in.readString();
    }

    public static final Creator<DtlMerchantMedia> CREATOR = new Creator<DtlMerchantMedia>() {
        @Override
        public DtlMerchantMedia createFromParcel(Parcel in) {
            return new DtlMerchantMedia(in);
        }

        @Override
        public DtlMerchantMedia[] newArray(int size) {
            return new DtlMerchantMedia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageId);
        dest.writeString(url);
        dest.writeString(logoUrl);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(logo);
    }
}
