package com.worldventures.dreamtrips.modules.feed.model;

import android.content.res.Resources;
import android.os.Parcel;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class FeedPhotoEventModel extends BaseEventModel<Photo> {

    public FeedPhotoEventModel() {
    }

    public FeedPhotoEventModel(Parcel in) {
        super(in);
    }

    @Override
    public String previewImage(Resources res) {
        int width = res.getDimensionPixelSize(R.dimen.bucket_cover_thumb_w);
        int height = res.getDimensionPixelSize(R.dimen.bucket_cover_thumb_h);
        if (getItem().getImages() != null) {
            return getItem().getImages().getUrl(width, height);
        } else return null;
    }

    public static final Creator<FeedPhotoEventModel> CREATOR = new Creator<FeedPhotoEventModel>() {
        @Override
        public FeedPhotoEventModel createFromParcel(Parcel in) {
            return new FeedPhotoEventModel(in);
        }

        @Override
        public FeedPhotoEventModel[] newArray(int size) {
            return new FeedPhotoEventModel[size];
        }
    };
}
