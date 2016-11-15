package com.worldventures.dreamtrips.modules.trips.model.filter;

import java.io.Serializable;

public class RegionHeaderModel implements Serializable {

   private boolean checked = true;
   private boolean hide = true;

   public boolean isChecked() {
      return checked;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   public boolean isHide() {
      return hide;
   }

   public void setHide(boolean hide) {
      this.hide = hide;
   }
}