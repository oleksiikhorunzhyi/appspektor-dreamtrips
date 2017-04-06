package com.worldventures.dreamtrips.api.friends.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.PaginatedParams;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface SearchParams extends PaginatedParams {
    @Nullable
    @SerializedName("query")
    String query();
}
