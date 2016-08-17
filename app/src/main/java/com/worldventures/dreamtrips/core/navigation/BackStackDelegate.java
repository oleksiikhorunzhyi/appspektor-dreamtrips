package com.worldventures.dreamtrips.core.navigation;

public class BackStackDelegate {

   private BackPressedListener listener;

   public boolean handleBackPressed() {
      return listener != null && listener.onBackPressed();
   }

   public void setListener(BackPressedListener listener) {
      this.listener = listener;
   }

   public void clearListener() {
      setListener(null);
   }

   public interface BackPressedListener {

      /**
       * Hook-method to perform some action when hardware 'back' button is pressed.<br />
       *
       * @return true if 'back' action was consumed, false if it needs to be handlet on upper level
       */
      boolean onBackPressed();
   }
}
