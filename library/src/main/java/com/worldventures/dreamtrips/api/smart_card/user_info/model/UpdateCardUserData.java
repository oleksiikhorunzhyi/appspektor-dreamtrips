package com.worldventures.dreamtrips.api.smart_card.user_info.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface UpdateCardUserData {
    @Nullable
    @SerializedName("displayName")
    String nameToDisplay();

    @Nullable
    @SerializedName("firstName")
    String firstName();

    @Nullable
    @SerializedName("middleName")
    String middleName();

    @Nullable
    @SerializedName("lastName")
    String lastName();

    @Nullable
    @SerializedName("displayPhoto")
    String photoUrl();
}
