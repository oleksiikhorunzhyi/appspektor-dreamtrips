package com.worldventures.dreamtrips.modules.feed.model;

import android.content.res.Resources;
import android.os.Parcel;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

public class FeedBucketEventModel extends BaseEventModel<BucketItem> {

    @Override
    public String previewImage(Resources res) {
        int width = res.getDimensionPixelSize(R.dimen.bucket_cover_thumb_w);
        int height = res.getDimensionPixelSize(R.dimen.bucket_cover_thumb_h);
        return getItem().getCoverUrl(width, height);
    }

    public FeedBucketEventModel() {
    }

    public FeedBucketEventModel(Parcel in) {
        super(in);
    }

    public static final Creator<FeedBucketEventModel> CREATOR = new Creator<FeedBucketEventModel>() {
        @Override
        public FeedBucketEventModel createFromParcel(Parcel in) {
            return new FeedBucketEventModel(in);
        }

        @Override
        public FeedBucketEventModel[] newArray(int size) {
            return new FeedBucketEventModel[size];
        }
    };
}
