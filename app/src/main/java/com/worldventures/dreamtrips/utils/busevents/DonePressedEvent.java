package com.worldventures.dreamtrips.utils.busevents;

import com.worldventures.dreamtrips.core.model.bucket.PopularBucketItem;

/**
 * Created by 1 on 17.03.15.
 */
public class DonePressedEvent {

    private PopularBucketItem popularBucketItem;
    private int position;

    public DonePressedEvent(PopularBucketItem popularBucketItem, int position) {
        this.popularBucketItem = popularBucketItem;
        this.position = position;
    }

    public PopularBucketItem getPopularBucketItem() {
        return popularBucketItem;
    }

    public int getPosition() {
        return position;
    }
}