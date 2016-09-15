package com.worldventures.dreamtrips.modules.trips.model;

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
