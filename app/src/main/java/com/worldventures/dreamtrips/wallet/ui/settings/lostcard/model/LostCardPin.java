package com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model;

import com.google.android.gms.maps.model.LatLng;

public class LostCardPin {

   private String place;
   private String address;
   private LatLng position;

   public LostCardPin(String place, String address, LatLng position) {
      this.place = place;
      this.address = address;
      this.position = position;
   }

   public String place() {
      return place;
   }

   public void setPlace(String place) {
      this.place = place;
   }

   public String address() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public LatLng position() {
      return position;
   }

   public void setPosition(LatLng position) {
      this.position = position;
   }
}
