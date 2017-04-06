package com.worldventures.dreamtrips.api.photos.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;
import com.worldventures.dreamtrips.api.session.model.Avatar;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface TaggedUser extends Identifiable<Integer> {
    @SerializedName("first_name")
    String firstName();

    @SerializedName("last_name")
    String lastName();

    @SerializedName("username")
    String username();

    @SerializedName("avatar")
    Avatar avatar();

    @SerializedName("badges")
    List<String> badges();

    @Nullable
    @SerializedName("location")
    String location();

    @Nullable
    @SerializedName("company")
    String company();
}
