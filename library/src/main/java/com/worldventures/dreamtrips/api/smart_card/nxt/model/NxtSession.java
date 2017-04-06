package com.worldventures.dreamtrips.api.smart_card.nxt.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface NxtSession {

    @SerializedName("nxt_token")
    String token();
}
