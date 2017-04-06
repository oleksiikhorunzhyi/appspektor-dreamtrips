package com.worldventures.dreamtrips.api.smart_card.association_info.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
@Deprecated
/**
 * @see
 * SmartCardInfo
 * com.worldventures.dreamtrips.api.smart_card.association_info.model.SmartCardUser
 */
public interface SmartCardData {

    @SerializedName("scID")
    long scId();

    @SerializedName("userID")
    long userId();

    @Nullable
    @SerializedName("displayName")
    String displayName();

    @Nullable
    @SerializedName("displayPhoto")
    String photoUrl();

    @Nullable
    @SerializedName("firstName")
    String firstName();

    @Nullable
    @SerializedName("middleName")
    String middleName();

    @Nullable
    @SerializedName("lastName")
    String lastName();
}
