package com.worldventures.dreamtrips.modules.friends.events;

import com.worldventures.dreamtrips.modules.common.model.User;

public class OpenFriendPrefsEvent {
   User friend;

   public OpenFriendPrefsEvent(User friend) {
      this.friend = friend;
   }

   public User getFriend() {
      return friend;
   }
}
