package com.worldventures.dreamtrips.api.hashtags.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.feed.model.FeedItemWrapper;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface HashtagsSearchResponse {
    @SerializedName("data")
    List<FeedItemWrapper> data();

    @SerializedName("metadata")
    Metadata metadata();
}
