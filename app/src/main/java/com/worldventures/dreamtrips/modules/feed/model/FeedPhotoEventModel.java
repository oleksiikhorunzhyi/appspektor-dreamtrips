package com.worldventures.dreamtrips.modules.feed.model;

import android.content.res.Resources;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class FeedPhotoEventModel extends BaseEventModel<Photo> {
    @Override
    public String previewImage(Resources res) {
        int width = res.getDimensionPixelSize(R.dimen.bucket_cover_thumb_w);
        int height = res.getDimensionPixelSize(R.dimen.bucket_cover_thumb_h);
        return getItem().getFSImage().getUrl(width, height);
    }
}
