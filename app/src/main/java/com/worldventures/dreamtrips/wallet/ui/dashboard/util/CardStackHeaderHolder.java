package com.worldventures.dreamtrips.wallet.ui.dashboard.util;

import android.net.Uri;

import com.techery.spares.adapter.HeaderItem;

import org.immutables.value.Value;

@Value.Immutable
public abstract class CardStackHeaderHolder implements HeaderItem {

   @Value.Default
   public int batteryLevel() {
      return 0;
   }

   @Value.Default
   public boolean connected() {
      return false;
   }

   @Value.Default
   public boolean lock() {
      return true;
   }

   @Value.Default
   public boolean stealthMode() {
      return false;
   }

   @Value.Default
   public String fullname() {
      return "";
   }

   @Value.Default
   public Uri photoUri() {
      return Uri.EMPTY;
   }

   @Value.Default
   public boolean firmwareUpdateAvailable() {
      return false;
   }

   @Value.Default
   public int cardCount() { return 0;}
}
