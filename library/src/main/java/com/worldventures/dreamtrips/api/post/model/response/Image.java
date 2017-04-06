package com.worldventures.dreamtrips.api.post.model.response;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface Image {

    @SerializedName("url")
    String url();
    @SerializedName("original_url")
    String originalUrl();

}
