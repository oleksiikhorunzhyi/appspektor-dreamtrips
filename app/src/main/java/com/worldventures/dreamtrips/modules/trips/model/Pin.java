package com.worldventures.dreamtrips.modules.trips.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.common.model.Coordinates;

import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class Pin {

   private List<String> tripUids;
   private Coordinates coordinates;

   public List<String> getTripUids() {
      return tripUids;
   }

   public void setTripUids(List<String> tripUids) {
      this.tripUids = tripUids;
   }

   public Coordinates getCoordinates() {
      return coordinates;
   }

   public void setCoordinates(Coordinates coordinates) {
      this.coordinates = coordinates;
   }
}
