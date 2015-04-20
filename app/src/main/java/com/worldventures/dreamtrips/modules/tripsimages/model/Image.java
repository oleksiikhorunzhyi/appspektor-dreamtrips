package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

public class Image extends BaseEntity implements Parcelable {

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    private boolean fromFile;
    private String url;

    public Image() {
    }

    private Image(Parcel in) {
        this.url = in.readString();
    }

    public String getUrl() {
        return url;
    }

    public String getUrl(int width, int height) {
        int size = Math.max(width, height);
        return url + String.format(UniversalImageLoader.PATTERN,
                size, size);
    }

    public String getThumbUrl(Resources resources) {
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.photo_thumb_size);
        return url + String.format(UniversalImageLoader.PATTERN,
                dimensionPixelSize, dimensionPixelSize);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFromFile(boolean fromFile) {
        this.fromFile = fromFile;
    }

    public boolean isFromFile() {
        return fromFile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
    }
}
