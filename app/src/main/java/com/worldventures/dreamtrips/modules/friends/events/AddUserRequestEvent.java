package com.worldventures.dreamtrips.modules.friends.events;

import com.worldventures.dreamtrips.modules.common.model.User;

public class AddUserRequestEvent {
   private User user;

   public AddUserRequestEvent(User user) {
      this.user = user;
   }

   public User getUser() {
      return user;
   }
}
