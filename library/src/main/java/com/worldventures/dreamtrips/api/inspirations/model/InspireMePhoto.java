package com.worldventures.dreamtrips.api.inspirations.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;
import com.worldventures.dreamtrips.api.photos.model.Image;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface InspireMePhoto extends Identifiable<Integer> {

    @SerializedName("quote")
    String quote();

    @SerializedName("author")
    String author();

    @SerializedName("images")
    Image image();
}
