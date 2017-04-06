package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface TransactionDetails extends Identifiable<String> {

    @SerializedName("credited_amount")
    Double creditedAmount();
    @SerializedName("current_balance")
    Double currentBalance();

}
