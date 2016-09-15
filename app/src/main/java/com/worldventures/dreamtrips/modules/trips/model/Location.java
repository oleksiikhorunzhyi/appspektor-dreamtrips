package com.worldventures.dreamtrips.modules.trips.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class Location implements Serializable, Parcelable {

   public static final Creator<Location> CREATOR = new Creator<Location>() {
      public Location createFromParcel(Parcel source) {
         return new Location(source);
      }

      public Location[] newArray(int size) {
         return new Location[size];
      }
   };

   protected String name;
   protected double lat;
   protected double lng;

   public Location() {
   }

   public Location(Location location) {
      if (location != null) {
         this.name = location.getName();
         this.lat = location.getLat();
         this.lng = location.getLng();
      }
   }

   public Location(double lat, double lng) {
      this.lat = lat;
      this.lng = lng;
   }

   public Location(android.location.Location location) {
      this.lat = location.getLatitude();
      this.lng = location.getLongitude();
   }

   private Location(Parcel in) {
      this.lat = in.readDouble();
      this.lng = in.readDouble();
      this.name = in.readString();
   }

   public double getLat() {
      return lat;
   }

   public void setLat(double lat) {
      this.lat = lat;
   }

   public double getLng() {
      return lng;
   }

   public void setLng(double lng) {
      this.lng = lng;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public LatLng asLatLng() {
      return new LatLng(lat, lng);
   }

   public android.location.Location asAndroidLocation() {
      android.location.Location androidLocation = new android.location.Location("");
      androidLocation.setLatitude(lat);
      androidLocation.setLongitude(lng);
      return androidLocation;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeDouble(this.lat);
      dest.writeDouble(this.lng);
      dest.writeString(this.name);
   }

   @Override
   public String toString() {
      return "Location{" +
            "name='" + name + '\'' +
            ", lat=" + lat +
            ", lng=" + lng +
            '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Location location = (Location) o;

      if (Double.compare(location.lat, lat) != 0) return false;
      if (Double.compare(location.lng, lng) != 0) return false;
      return !(name != null ? !name.equals(location.name) : location.name != null);
   }

   @Override
   public int hashCode() {
      int result;
      long temp;
      result = name != null ? name.hashCode() : 0;
      temp = Double.doubleToLongBits(lat);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(lng);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      return result;
   }
}
