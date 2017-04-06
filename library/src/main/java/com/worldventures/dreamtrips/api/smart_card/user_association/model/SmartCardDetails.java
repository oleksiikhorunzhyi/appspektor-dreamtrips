package com.worldventures.dreamtrips.api.smart_card.user_association.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Date;

@Gson.TypeAdapters
@Value.Immutable
public interface SmartCardDetails {

    @SerializedName("serialNumber")
    String serialNumber();

    @SerializedName("scID")
    long scID();

    @SerializedName("bleAddress")
    String bleAddress();

    @SerializedName("revVersion")
    String revVersion();

    @SerializedName("wvOrderId")
    String wvOrderId();

    @SerializedName("nxtOrderId")
    String nxtOrderId();

    @SerializedName("orderDate")
    Date orderDate();

}
