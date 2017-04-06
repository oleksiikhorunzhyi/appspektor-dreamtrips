package com.worldventures.dreamtrips.api.photos.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface PhotoTag extends Identifiable<Integer> {
    @SerializedName("position")
    PhotoTagPosition position();
    @SerializedName("user")
    TaggedUser user();
}
