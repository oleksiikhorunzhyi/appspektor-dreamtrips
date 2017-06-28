package com.worldventures.dreamtrips.wallet.service.nxt.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface MultiRequestElement {

   @SerializedName("Operation")
   String operation();
   @SerializedName("TokenName")
   String tokenName();
   @SerializedName("Value")
   String value();
   @SerializedName("RefId")
   String referenceId();

}