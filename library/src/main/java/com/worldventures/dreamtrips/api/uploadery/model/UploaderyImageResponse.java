package com.worldventures.dreamtrips.api.uploadery.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface UploaderyImageResponse {

    @SerializedName("file")
    UploaderyImage uploaderyPhoto();

}
