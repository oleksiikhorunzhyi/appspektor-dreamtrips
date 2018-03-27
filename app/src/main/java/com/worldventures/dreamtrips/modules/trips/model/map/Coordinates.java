package com.worldventures.dreamtrips.modules.trips.model.map;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class Coordinates implements Serializable {

   private Double lat;
   private Double lng;

   public Coordinates() {
      // This constructor is intentionally empty. Nothing special is needed here.
   }

   public Coordinates(Double lat, Double lng) {
      this.lat = lat;
      this.lng = lng;
   }

   public Double getLat() {
      return lat;
   }

   public void setLat(Double lat) {
      this.lat = lat;
   }

   public Double getLng() {
      return lng;
   }

   public void setLng(Double lng) {
      this.lng = lng;
   }
}
