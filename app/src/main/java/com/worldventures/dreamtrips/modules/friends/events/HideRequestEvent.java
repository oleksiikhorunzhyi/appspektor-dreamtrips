package com.worldventures.dreamtrips.modules.friends.events;

import com.worldventures.dreamtrips.modules.common.model.User;

public class HideRequestEvent {
   private User user;

   public HideRequestEvent(User user) {
      this.user = user;
   }

   public User getUser() {
      return user;
   }
}
