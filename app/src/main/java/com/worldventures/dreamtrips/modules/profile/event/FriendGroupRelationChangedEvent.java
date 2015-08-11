package com.worldventures.dreamtrips.modules.profile.event;

import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.model.Friend;

public class FriendGroupRelationChangedEvent {

    Circle circle;
    Friend friend;
    State state;

    public FriendGroupRelationChangedEvent(Friend friend, Circle circle, State state) {
        this.friend = friend;
        this.circle = circle;
        this.state = state;
    }

    public enum State {
        REMOVED, ADDED
    }

    public Circle getCircle() {
        return circle;
    }

    public State getState() {
        return state;
    }

    public Friend getFriend() {
        return friend;
    }
}
