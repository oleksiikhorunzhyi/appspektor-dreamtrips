package com.worldventures.dreamtrips.modules.profile;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.model.Friend;

public class FriendGroupRelation {

    Circle circle;
    Friend friend;

    public FriendGroupRelation(Circle circle, Friend friend) {
        this.circle = circle;
        this.friend = friend;
    }

    public Circle circle() {
        return circle;
    }

    public Friend friend() {
        return friend;
    }

    public boolean isFriendInCircle() {
        return Queryable.from(friend.getCircleIds()).any(element -> {
            return element.equals(circle.getId());
        });
    }

}
