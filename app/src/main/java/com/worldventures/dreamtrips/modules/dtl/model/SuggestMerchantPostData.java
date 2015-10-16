package com.worldventures.dreamtrips.modules.dtl.model;

public class SuggestMerchantPostData {

    public final String contactName;
    public final String phone;
    public final ContactTime contactTime;
    public final RateContainer rate;
    public final String description;

    public SuggestMerchantPostData(String contactName, String phone, ContactTime contactTime, RateContainer rate, String description) {
        this.contactName = contactName;
        this.phone = phone;
        this.contactTime = contactTime;
        this.rate = rate;
        this.description = description;
    }

}
