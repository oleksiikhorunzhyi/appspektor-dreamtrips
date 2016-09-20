package com.worldventures.dreamtrips.modules.trips.model.filter;

import java.io.Serializable;

public class BoolFilter implements Serializable {

   boolean active;

   public boolean isActive() {
      return active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }
}
