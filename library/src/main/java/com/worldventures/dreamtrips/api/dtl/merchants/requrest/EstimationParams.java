package com.worldventures.dreamtrips.api.dtl.merchants.requrest;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface EstimationParams {

    @SerializedName("bill_total")
    Double billTotal();
    @SerializedName("checkin_time")
    String checkinTime();
    @SerializedName("currency_code")
    String currencyCode();

}
