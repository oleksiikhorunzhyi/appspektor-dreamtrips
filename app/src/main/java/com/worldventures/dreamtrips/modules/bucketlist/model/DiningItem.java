package com.worldventures.dreamtrips.modules.bucketlist.model;

import android.os.Parcel;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DiningItem extends BaseEntity {

   private String name;
   private String country;
   private String city;
   private String address;
   private String phone_number;
   private String url;
   private String description;
   private String priceRange;

   public DiningItem() {
      super();
   }

   public String getName() {
      return name;
   }

   public String getCountry() {
      return country;
   }

   public String getCity() {
      return city;
   }

   public String getAddress() {
      return address;
   }

   public String getPhoneNumber() {
      return phone_number;
   }

   public String getPriceRange() {
      return priceRange;
   }

   public String getUrl() {
      return url;
   }

   public String getDescription() {
      return description;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setCountry(String country) {
      this.country = country;
   }

   public void setCity(String city) {
      this.city = city;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phone_number = phoneNumber;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setPriceRange(String priceRange) {
      this.priceRange = priceRange;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeString(this.name);
      dest.writeString(this.country);
      dest.writeString(this.city);
      dest.writeString(this.address);
      dest.writeString(this.phone_number);
      dest.writeString(this.url);
      dest.writeString(this.description);
      dest.writeString(this.priceRange);
   }

   public DiningItem(Parcel in) {
      super(in);
      this.name = in.readString();
      this.country = in.readString();
      this.city = in.readString();
      this.address = in.readString();
      this.phone_number = in.readString();
      this.url = in.readString();
      this.description = in.readString();
      this.priceRange = in.readString();
   }

   public static final Creator<DiningItem> CREATOR = new Creator<DiningItem>() {
      @Override
      public DiningItem createFromParcel(Parcel in) {
         return new DiningItem(in);
      }

      @Override
      public DiningItem[] newArray(int size) {
         return new DiningItem[size];
      }
   };
}
