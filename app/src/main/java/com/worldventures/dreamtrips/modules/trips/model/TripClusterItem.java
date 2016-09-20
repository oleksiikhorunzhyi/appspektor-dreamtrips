package com.worldventures.dreamtrips.modules.trips.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class TripClusterItem implements ClusterItem {

   private Pin pin;

   public TripClusterItem(Pin pin) {
      this.pin = pin;
   }

   @Override
   public LatLng getPosition() {
      return new LatLng(pin.getCoordinates().getLat(), pin.getCoordinates().getLng());
   }

   public Pin getPin() {
      return pin;
   }
}
