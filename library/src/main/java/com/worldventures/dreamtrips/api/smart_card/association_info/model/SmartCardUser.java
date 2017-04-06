package com.worldventures.dreamtrips.api.smart_card.association_info.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface SmartCardUser {

    @SerializedName("userID")
    String userId();

    @SerializedName("firstName")
    String firstName();

    @SerializedName("lastName")
    @Nullable
    String lastName();

    @SerializedName("middleName")
    @Nullable
    String middleName();

    @SerializedName("displayPhoto")
    @Nullable
    String displayPhoto();
}
