package com.worldventures.dreamtrips.api.circles.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface Circle extends Identifiable<String> {

    @SerializedName("name")
    String name();

    @SerializedName("predefined")
    boolean predefined();

}
