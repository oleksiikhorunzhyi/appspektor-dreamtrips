package com.worldventures.dreamtrips.api.uploadery.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface UploaderyImage {

    @SerializedName("fieldname")
    String fieldName();
    @SerializedName("originalname")
    String originalName();
    @SerializedName("encoding")
    String encoding();
    @SerializedName("mimetype")
    String mimeType();
    @SerializedName("location")
    String location();

}
