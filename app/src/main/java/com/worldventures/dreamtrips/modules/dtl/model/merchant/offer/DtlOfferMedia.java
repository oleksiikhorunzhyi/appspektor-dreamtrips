package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.core.ui.fragment.ImagePathHolder;

public class DtlOfferMedia implements Parcelable, ImagePathHolder {

    private String id;
    private String name;
    private String description;
    private String category;
    private String url;
    private String order;
    private int width;
    private int height;

    @Override
    public String getImagePath() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getUrl() {
        return url;
    }

    public String getOrder() {
        return order;
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
    protected DtlOfferMedia(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        category = in.readString();
        url = in.readString();
        order = in.readString();
        width = in.readInt();
        height = in.readInt();
    }

    public static final Creator<DtlOfferMedia> CREATOR = new Creator<DtlOfferMedia>() {
        @Override
        public DtlOfferMedia createFromParcel(Parcel in) {
            return new DtlOfferMedia(in);
        }

        @Override
        public DtlOfferMedia[] newArray(int size) {
            return new DtlOfferMedia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeString(url);
        dest.writeString(order);
        dest.writeInt(width);
        dest.writeInt(height);
    }
}
