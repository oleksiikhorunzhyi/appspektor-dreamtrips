package com.worldventures.dreamtrips.wallet.service.lostcard.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface LocationPlace {

   @SerializedName("place_id")
   String placeId();

   String name();

   String vicinity();
}
