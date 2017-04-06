package com.worldventures.dreamtrips.api.messenger.model.response;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;
import com.worldventures.dreamtrips.api.session.model.Avatar;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface ShortUserProfile extends Identifiable<Integer> {

    @SerializedName("username")
    String username();

    @SerializedName("first_name")
    String firstName();

    @SerializedName("last_name")
    String lastName();

    @SerializedName("company")
    @Nullable
    String company();

    @SerializedName("avatar")
    Avatar avatar();

    @Nullable
    @SerializedName("location")
    String location();

    @SerializedName("badges")
    List<String> badges();
}
