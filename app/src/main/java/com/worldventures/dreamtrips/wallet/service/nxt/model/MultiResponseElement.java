package com.worldventures.dreamtrips.wallet.service.nxt.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface MultiResponseElement {

   @SerializedName("RefId")
   String referenceId();

   @Nullable
   @SerializedName("Value")
   String value();

   @Nullable
   @SerializedName("Error")
   MultiErrorResponse error();

}