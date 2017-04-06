package com.worldventures.dreamtrips.api.friends.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.PaginatedParams;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface MutualFriendsParams extends PaginatedParams {

    @SerializedName("user_id")
    int userId();
}
