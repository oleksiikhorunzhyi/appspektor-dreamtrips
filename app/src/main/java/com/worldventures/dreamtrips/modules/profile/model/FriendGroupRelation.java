package com.worldventures.dreamtrips.modules.profile.model;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

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
      if (friend.getCircles() == null) return false;
      //
      return Queryable.from(friend.getCircles()).any(element -> {
         return element.equals(circle);
      });
   }

}
