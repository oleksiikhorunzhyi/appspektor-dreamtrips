package com.worldventures.dreamtrips.modules.friends.events;

public class RequestsLoadedEvent {

   int count;

   public RequestsLoadedEvent(int count) {
      this.count = count;
   }

   public int getCount() {
      return count;
   }
}
