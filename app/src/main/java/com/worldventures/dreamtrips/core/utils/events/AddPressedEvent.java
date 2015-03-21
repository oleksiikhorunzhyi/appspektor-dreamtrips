package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.core.model.bucket.PopularBucketItem;

public class AddPressedEvent {

    private PopularBucketItem popularBucketItem;
    private int position;

    public AddPressedEvent(PopularBucketItem popularBucketItem, int position) {
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