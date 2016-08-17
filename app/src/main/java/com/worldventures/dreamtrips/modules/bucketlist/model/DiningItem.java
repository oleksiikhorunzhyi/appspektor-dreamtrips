package com.worldventures.dreamtrips.modules.bucketlist.model;

import android.os.Parcel;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DiningItem extends BaseEntity {

   String name;
   String country;
   String city;
   String address;
   String cuisine;
   @SerializedName("phone_number") String phone_number;
   String url;
   String description;
   @SerializedName("short_description") String shortDescription;
   @SerializedName("cover_photo") DiningCoverPhoto coverPhoto;
   @SerializedName("price_range") String priceRange;

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

   public String getCuisine() {
      return cuisine;
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

   public String getShortDescription() {
      return shortDescription;
   }

   public DiningCoverPhoto getCoverPhoto() {
      return coverPhoto;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeString(this.name);
      dest.writeString(this.country);
      dest.writeString(this.city);
      dest.writeString(this.address);
      dest.writeString(this.cuisine);
      dest.writeString(this.phone_number);
      dest.writeString(this.url);
      dest.writeString(this.description);
      dest.writeString(this.shortDescription);
      dest.writeSerializable(this.coverPhoto);
      dest.writeString(this.priceRange);
   }

   public DiningItem(Parcel in) {
      super(in);
      this.name = in.readString();
      this.country = in.readString();
      this.city = in.readString();
      this.address = in.readString();
      this.cuisine = in.readString();
      this.phone_number = in.readString();
      this.url = in.readString();
      this.description = in.readString();
      this.shortDescription = in.readString();
      this.coverPhoto = (DiningCoverPhoto) in.readSerializable();
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
