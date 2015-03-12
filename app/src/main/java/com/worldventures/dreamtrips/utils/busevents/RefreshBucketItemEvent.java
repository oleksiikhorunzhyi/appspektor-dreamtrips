package com.worldventures.dreamtrips.utils.busevents;

import com.worldventures.dreamtrips.core.model.bucket.BucketPostItem;

/**
 * Created by 1 on 11.03.15.
 */
public class RefreshBucketItemEvent {

    public BucketPostItem postItem;

    public RefreshBucketItemEvent(BucketPostItem postItem) {
        this.postItem = postItem;
    }

    public BucketPostItem getPostItem() {
        return postItem;
    }

    public void setPostItem(BucketPostItem postItem) {
        this.postItem = postItem;
    }
}
