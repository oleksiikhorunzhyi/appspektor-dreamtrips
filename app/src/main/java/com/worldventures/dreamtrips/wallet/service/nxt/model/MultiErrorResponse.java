package com.worldventures.dreamtrips.wallet.service.nxt.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface MultiErrorResponse {

   @SerializedName("Code")
   int code();

   @SerializedName("Message")
   String message();

}
