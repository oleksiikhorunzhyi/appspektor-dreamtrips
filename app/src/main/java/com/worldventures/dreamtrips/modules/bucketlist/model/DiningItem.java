package com.worldventures.dreamtrips.modules.bucketlist.model;

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
    @SerializedName("phone_number")
    String phone_number;
    String url;
    String description;
    @SerializedName("short_description")
    String shortDescription;
    @SerializedName("cover_photo")
    DiningCoverPhoto coverPhoto;
    @SerializedName("price_range")
    String priceRange;

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
}
