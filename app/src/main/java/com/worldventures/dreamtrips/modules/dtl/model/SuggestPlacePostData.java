package com.worldventures.dreamtrips.modules.dtl.model;

public class SuggestPlacePostData {

    public final String placeId;
    public final String name;
    public final String city;
    public final String contactName;
    public final String phone;
    public final ContactTime contactTime;
    public final RateContainer rate;
    public final String description;

    public SuggestPlacePostData(String placeId, String contactName, String phone, ContactTime contactTime,
                                RateContainer rate, String description) {
        this.placeId = placeId;
        this.name = null;
        this.city = null;
        this.contactName = contactName;
        this.phone = phone;
        this.contactTime = contactTime;
        this.rate = rate;
        this.description = description;
    }

    public SuggestPlacePostData(String restaurantName, String city, String contactName, String phone,
                                ContactTime contactTime, RateContainer rate, String description) {
        this.placeId = null;
        this.name = restaurantName;
        this.city = city;
        this.contactName = contactName;
        this.phone = phone;
        this.contactTime = contactTime;
        this.rate = rate;
        this.description = description;
    }
}
