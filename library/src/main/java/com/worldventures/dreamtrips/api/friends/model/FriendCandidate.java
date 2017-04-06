package com.worldventures.dreamtrips.api.friends.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;
import com.worldventures.dreamtrips.api.session.model.Avatar;
import com.worldventures.dreamtrips.api.session.model.MutualFriends;
import com.worldventures.dreamtrips.api.session.model.Relationship;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface FriendCandidate extends Identifiable<Integer> {

    @SerializedName("username")
    String username();

    @SerializedName("first_name")
    String firstName();

    @SerializedName("last_name")
    String lastName();

    @SerializedName("avatar")
    Avatar avatar();

    @Nullable
    @SerializedName("location")
    String location();

    @Nullable
    @SerializedName("company")
    String company();

    @SerializedName("badges")
    List<String> badges();

    @Nullable
    @SerializedName("mutuals")
    MutualFriends mutuals();

    @SerializedName("relationship")
    @Nullable
    Relationship relationship();
}
