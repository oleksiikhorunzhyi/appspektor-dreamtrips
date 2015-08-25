package com.worldventures.dreamtrips.modules.profile.event.profilecell;

public class OnBucketListClickedEvent {


    int userId;

    public OnBucketListClickedEvent(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}
