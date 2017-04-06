package com.worldventures.dreamtrips.api.dtl.merchants.requrest;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;


@Gson.TypeAdapters
@Value.Immutable
public interface Transaction {

    @SerializedName("receipt_photo_url")
    String receiptPhotoUrl();
    @SerializedName("checkin_time")
    String checkinTime();
    @SerializedName("merchant_token")
    String merchantToken();
    @SerializedName("bill_total")
    Double billTotal();
    @SerializedName("currency_code")
    String currencyCode();
    @SerializedName("location")
    Location location();
}
