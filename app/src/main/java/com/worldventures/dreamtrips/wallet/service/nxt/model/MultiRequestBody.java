package com.worldventures.dreamtrips.wallet.service.nxt.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface MultiRequestBody {

   @Nullable
   @SerializedName("SessionToken")
   String sessionToken();

   @SerializedName("MultiRequestElement")
   List<MultiRequestElement> multiRequestElements();
}