package com.worldventures.dreamtrips.wallet.service.lostcard.command.http.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

@Value.Immutable
public interface ApiPlace {

   @SerializedName("place_id")
   String placeId();

   String name();

   String vicinity();
}
