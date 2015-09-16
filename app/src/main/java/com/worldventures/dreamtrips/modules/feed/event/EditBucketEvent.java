package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

public class EditBucketEvent {

    private String uid;
    private BucketItem.BucketType type;

    public EditBucketEvent(String uid, BucketItem.BucketType type) {
        this.uid = uid;
        this.type = type;
    }

    public BucketItem.BucketType getType() {
        return type;
    }

    public String getUid() {
        return uid;
    }
}
