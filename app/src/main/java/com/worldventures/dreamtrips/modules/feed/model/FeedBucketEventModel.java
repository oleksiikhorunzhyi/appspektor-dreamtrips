package com.worldventures.dreamtrips.modules.feed.model;

import android.content.res.Resources;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

public class FeedBucketEventModel extends BaseEventModel<BucketItem> {


    @Override
    public String previewImage(Resources res) {
        int width = res.getDimensionPixelSize(R.dimen.bucket_cover_thumb_w);
        int height = res.getDimensionPixelSize(R.dimen.bucket_cover_thumb_h);
        return getItem().getCoverUrl(width, height);
    }
}
