package com.worldventures.dreamtrips.api.feedback.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface FeedbackAttachment {
    @SerializedName("origin_url")
    String originUrl();
    @SerializedName("type")
    FeedbackType type();

    enum FeedbackType {
        @SerializedName("image")IMAGE
    }
}
