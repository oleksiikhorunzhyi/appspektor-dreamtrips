package com.worldventures.dreamtrips.api.hashtags.model;


import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.feed.model.FeedParams;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface HashtagsSearchParams extends FeedParams {

    @SerializedName("type")
    Type type();

    @SerializedName("query")
    String query();

    enum Type {
        @SerializedName("post")
        POST("post"),
        UNKNOWN(null);

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
