package com.worldventures.dreamtrips.api.smart_card.location.model;


import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface SmartCardCoordinates {

    @SerializedName("lat")
    Double lat();
    @SerializedName("lng")
    Double lng();
}
