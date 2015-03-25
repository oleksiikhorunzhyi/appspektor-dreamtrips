package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;

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