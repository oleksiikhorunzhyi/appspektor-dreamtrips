package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

@DefaultSerializer(TaggedFieldSerializer.class)
public class DiningItem extends BaseEntity {

    @TaggedFieldSerializer.Tag(1)
    String name;

    @TaggedFieldSerializer.Tag(2)
    String country;

    @TaggedFieldSerializer.Tag(3)
    String city;

    @TaggedFieldSerializer.Tag(4)
    String address;

    @TaggedFieldSerializer.Tag(5)
    String cuisine;

    @SerializedName("phone_number")
    @TaggedFieldSerializer.Tag(6)
    String phone_number;

    @TaggedFieldSerializer.Tag(7)
    String url;

    @TaggedFieldSerializer.Tag(8)
    String description;

    @TaggedFieldSerializer.Tag(9)
    @SerializedName("short_description")
    String shortDescription;

    @TaggedFieldSerializer.Tag(10)
    @SerializedName("cover_photo")
    DiningCoverPhoto coverPhoto;

    @TaggedFieldSerializer.Tag(11)
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
