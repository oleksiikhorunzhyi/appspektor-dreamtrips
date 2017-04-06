package com.worldventures.dreamtrips.api.messenger.model.request;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface GetShortUserProfileBody {

    @Value.Parameter
    @SerializedName("usernames")
    List<String> usernames();

}
