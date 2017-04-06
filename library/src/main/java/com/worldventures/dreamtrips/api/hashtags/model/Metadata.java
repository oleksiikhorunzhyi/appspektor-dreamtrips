package com.worldventures.dreamtrips.api.hashtags.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface Metadata {
    @SerializedName("hashtags")
    List<HashTagSimple> hashtags();
}
