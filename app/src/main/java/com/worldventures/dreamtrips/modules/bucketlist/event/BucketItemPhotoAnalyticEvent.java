package com.worldventures.dreamtrips.modules.bucketlist.event;

public class BucketItemPhotoAnalyticEvent {

    String actionAttribute;

    public BucketItemPhotoAnalyticEvent(String actionAttribute) {
        this.actionAttribute = actionAttribute;
    }

    public String getActionAttribute() {
        return actionAttribute;
    }
}
