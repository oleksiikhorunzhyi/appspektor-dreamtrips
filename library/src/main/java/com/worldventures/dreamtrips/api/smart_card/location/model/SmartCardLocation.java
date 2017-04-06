package com.worldventures.dreamtrips.api.smart_card.location.model;


import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@Gson.TypeAdapters
@Value.Immutable
public interface SmartCardLocation {

    @Nullable
    @SerializedName("coordinates")
    SmartCardCoordinates coordinates();

    @SerializedName("created_at")
    Date createdAt();

    @SerializedName("type")
    SmartCardLocationType type();
}
