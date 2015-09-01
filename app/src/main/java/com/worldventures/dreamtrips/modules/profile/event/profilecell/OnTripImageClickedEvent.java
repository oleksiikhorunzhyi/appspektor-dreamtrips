package com.worldventures.dreamtrips.modules.profile.event.profilecell;

public class OnTripImageClickedEvent {

    int userId;

    public OnTripImageClickedEvent(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}
