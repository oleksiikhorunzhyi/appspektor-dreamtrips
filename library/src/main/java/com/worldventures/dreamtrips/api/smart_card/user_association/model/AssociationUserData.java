package com.worldventures.dreamtrips.api.smart_card.user_association.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface AssociationUserData {

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
    String displayPhoto();
}
