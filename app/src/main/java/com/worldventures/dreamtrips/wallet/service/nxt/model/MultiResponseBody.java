package com.worldventures.dreamtrips.wallet.service.nxt.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface MultiResponseBody {

   @SerializedName("MultiResponseElement")
   List<MultiResponseElement> multiResponseElements();

}