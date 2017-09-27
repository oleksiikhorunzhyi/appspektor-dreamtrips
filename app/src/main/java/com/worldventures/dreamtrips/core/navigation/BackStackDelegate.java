package com.worldventures.dreamtrips.core.navigation;

import java.util.LinkedList;

public class BackStackDelegate {

   private BackPressedListener listener;

   private final LinkedList<BackPressedListener> listeners;

   public BackStackDelegate() {
      this.listeners = new LinkedList<>();
   }

   public boolean handleBackPressed() {
      return (listener != null && listener.onBackPressed()) || (!listeners.isEmpty() && executeListeners());
   }

   private boolean executeListeners() {
      boolean supportNext = false;
      for (BackPressedListener listener : listeners) {
         if (listener.onBackPressed()) {
            supportNext = true;
         }
      }
      return supportNext;
   }

   @Deprecated
   public void setListener(BackPressedListener listener) {
      this.listener = listener;
   }

   @Deprecated
   public void clearListener() {
      listener = null;
   }

   public void clearListeners() {
      listeners.clear();
   }

   public void addListener(BackPressedListener listener) {
      if (!listeners.contains(listener)) {
         listeners.add(listener);
      }
   }

   public void removeListener(BackPressedListener listener) {
      if (listeners.contains(listener)) {
         listeners.remove(listener);
      }
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
