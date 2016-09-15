package com.worldventures.dreamtrips.modules.friends.events;


import com.worldventures.dreamtrips.modules.common.model.User;

public class StartSingleChatEvent {
   User friend;

   public StartSingleChatEvent(User friend) {
      this.friend = friend;
   }

   public User getFriend() {
      return friend;
   }

}
