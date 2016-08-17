package com.worldventures.dreamtrips.modules.friends.events;

import com.worldventures.dreamtrips.modules.common.model.User;

public class UserClickedEvent {
   private User user;

   public UserClickedEvent(User user) {
      this.user = user;
   }

   public User getUser() {
      return user;
   }
}
