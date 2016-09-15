package com.worldventures.dreamtrips.modules.friends.events;

import com.worldventures.dreamtrips.modules.common.model.User;

public class RemoveUserEvent {

   private User user;

   public RemoveUserEvent(User user) {
      this.user = user;
   }

   public User getUser() {
      return user;
   }
}
