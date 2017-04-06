package com.worldventures.dreamtrips.api.feed.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface FeedItemWrapper {

    @SerializedName("type")
    Type type();
    @SerializedName("items")
    List<FeedItem> items();

    enum Type {
        @SerializedName("Single")SINGLE,
        @SerializedName("Group")GROUP,

        UNKNOWN
    }
}
