package com.worldventures.dreamtrips.wallet.service.lostcard.command.http.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;


@Value.Immutable
@Gson.TypeAdapters
public interface AddressRestResponse {

   @SerializedName("results")
   List<AddressComponents> results();
   @SerializedName("place_id")
   @Nullable
   String placeId();
   @SerializedName("status")
   String status();

   @Value.Immutable
   @Gson.TypeAdapters
   interface AddressComponents {
      @SerializedName("address_components")
      List<AddComponent> components();
      @SerializedName("formatted_address")
      String formattedAddress();
      @SerializedName("types")
      List<String> types();
   }

   @Value.Immutable
   @Gson.TypeAdapters
   interface AddComponent {
      @SerializedName("long_name")
      String longName();
      @SerializedName("short_name")
      String shortName();
      @SerializedName("types")
      List<String> types();
   }
}
