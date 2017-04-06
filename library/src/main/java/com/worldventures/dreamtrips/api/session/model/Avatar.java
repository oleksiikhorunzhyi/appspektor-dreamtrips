package com.worldventures.dreamtrips.api.session.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface Avatar {

    @SerializedName("original")
    String original();

    @SerializedName("medium")
    String medium();

    @SerializedName("thumb")
    String thumb();
}
