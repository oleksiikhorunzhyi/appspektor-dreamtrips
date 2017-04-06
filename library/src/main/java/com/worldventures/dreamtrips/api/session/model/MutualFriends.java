package com.worldventures.dreamtrips.api.session.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface MutualFriends {

    @SerializedName("count")
    int count();
}
