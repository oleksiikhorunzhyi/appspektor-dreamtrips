package com.worldventures.dreamtrips.modules.friends.events;

import com.worldventures.dreamtrips.modules.friends.model.Friend;

public class OpenFriendPrefsEvent {
    Friend friend;

    public OpenFriendPrefsEvent(Friend friend) {
        this.friend = friend;
    }

    public Friend getFriend() {
        return friend;
    }
}
