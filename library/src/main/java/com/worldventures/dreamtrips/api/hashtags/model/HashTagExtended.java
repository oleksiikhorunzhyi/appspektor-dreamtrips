package com.worldventures.dreamtrips.api.hashtags.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface HashTagExtended extends Hashtag {
    @SerializedName("usage_count")
    Integer usageCount();
}
