package com.worldventures.dreamtrips.modules.friends.events;

import com.worldventures.dreamtrips.modules.common.model.User;

public class UnfriendEvent {

   private User friend;

   public UnfriendEvent(User friend) {
      this.friend = friend;
   }

   public User getFriend() {
      return friend;
   }
}
