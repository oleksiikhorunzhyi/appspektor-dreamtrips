package com.worldventures.dreamtrips.api.ysbh.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;
import com.worldventures.dreamtrips.api.photos.model.Image;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface YSBHPhoto extends Identifiable<Integer> {
    @SerializedName("title")
    String title();

    @SerializedName("images")
    Image image();
}
