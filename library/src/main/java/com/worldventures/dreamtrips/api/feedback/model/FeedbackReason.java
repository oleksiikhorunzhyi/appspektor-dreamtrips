package com.worldventures.dreamtrips.api.feedback.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface FeedbackReason extends Identifiable<Integer> {
    @SerializedName("text")
    String text();
}
