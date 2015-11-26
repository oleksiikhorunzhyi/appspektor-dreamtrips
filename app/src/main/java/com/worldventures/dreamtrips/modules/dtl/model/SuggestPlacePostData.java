package com.worldventures.dreamtrips.modules.dtl.model;

public class SuggestPlacePostData {

    public String placeId;
    public String name;
    public String city;
    public String contactName;
    public String phone;
    public ContactTime contactTime;
    public RateContainer rate;
    public String description;

    public SuggestPlacePostData(String placeId, String restaurantName, String city, String contactName, String phone, ContactTime contactTime,
                                RateContainer rate, String description) {
        this(restaurantName, city, contactName, phone, contactTime, rate, description);
        this.placeId = placeId;
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
