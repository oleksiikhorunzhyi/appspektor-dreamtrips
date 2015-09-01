package com.worldventures.dreamtrips.modules.profile.event;

import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.common.model.User;

public class FriendGroupRelationChangedEvent {

    Circle circle;
    User friend;
    State state;

    public FriendGroupRelationChangedEvent(User friend, Circle circle, State state) {
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

    public User getFriend() {
        return friend;
    }
}
