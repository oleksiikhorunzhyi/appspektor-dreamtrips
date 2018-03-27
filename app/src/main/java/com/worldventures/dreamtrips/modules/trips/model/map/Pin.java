package com.worldventures.dreamtrips.modules.trips.model.map;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import java.io.Serializable;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class Pin implements Serializable {

   private Coordinates coordinates;
   private List<String> tripUids;

   public Pin() {
      // This constructor is intentionally empty. Nothing special is needed here.
   }

   public Pin(Coordinates coordinates, List<String> tripUids) {
      this.coordinates = coordinates;
      this.tripUids = tripUids;
   }

   public Coordinates getCoordinates() {
      return coordinates;
   }

   public void setCoordinates(Coordinates coordinates) {
      this.coordinates = coordinates;
   }

   public List<String> getTripUids() {
      return tripUids;
   }

   public void setTripUids(List<String> tripUids) {
      this.tripUids = tripUids;
   }
}
