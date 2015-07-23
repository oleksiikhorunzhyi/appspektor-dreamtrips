package com.worldventures.dreamtrips.modules.friends.events;

import com.worldventures.dreamtrips.modules.friends.model.Friend;

public class UnfriendEvent {

    private Friend friend;

    public UnfriendEvent(Friend friend) {
        this.friend = friend;
    }

    public Friend getFriend() {
        return friend;
    }
}
