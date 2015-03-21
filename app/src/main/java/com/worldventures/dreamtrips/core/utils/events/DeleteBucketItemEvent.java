package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

/**
 * Created by 1 on 02.03.15.
 */
public class DeleteBucketItemEvent {

    private BucketItem bucketItem;
    private int position;


    public DeleteBucketItemEvent(BucketItem bucketItem, int position) {
        this.bucketItem = bucketItem;
        this.position = position;
    }

    public BucketItem getBucketItem() {
        return bucketItem;
    }

    public int getPosition() {
        return position;
    }
}
