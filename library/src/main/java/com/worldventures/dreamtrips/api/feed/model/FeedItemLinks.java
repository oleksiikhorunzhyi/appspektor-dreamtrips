package com.worldventures.dreamtrips.api.feed.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.messenger.model.response.ShortUserProfile;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface FeedItemLinks {

    @SerializedName("users")
    List<ShortUserProfile> users();
}
