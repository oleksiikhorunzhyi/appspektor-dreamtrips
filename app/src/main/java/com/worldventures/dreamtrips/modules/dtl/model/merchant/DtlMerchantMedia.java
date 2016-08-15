package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantMedia;
import com.worldventures.dreamtrips.core.ui.fragment.ImagePathHolder;

public class DtlMerchantMedia implements Parcelable, ImagePathHolder {

    private String url;
    private String category;
    private int width;
    private int height;

    public DtlMerchantMedia() {
    }

    public DtlMerchantMedia(MerchantMedia merchantMedia) {
        url = merchantMedia.url();
        category = merchantMedia.category();
        width = merchantMedia.width();
        height = merchantMedia.height();
    }

    @Override
    public String getImagePath() {
        return url;
    }

    public String getCategory() {
        return category;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected DtlMerchantMedia(Parcel in) {
        url = in.readString();
        category = in.readString();
        width = in.readInt();
        height = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(category);
        dest.writeInt(width);
        dest.writeInt(height);
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
}
