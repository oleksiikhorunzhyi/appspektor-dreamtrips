package com.worldventures.dreamtrips.modules.profile;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.common.model.User;

public class FriendGroupRelation {

    Circle circle;
    User friend;

    public FriendGroupRelation(Circle circle, User friend) {
        this.circle = circle;
        this.friend = friend;
    }

    public Circle circle() {
        return circle;
    }

    public User friend() {
        return friend;
    }

    public boolean isFriendInCircle() {
        return Queryable.from(friend.getCircleIds()).any(element -> {
            return element.equals(circle.getId());
        });
    }

}
