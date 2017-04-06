package com.worldventures.dreamtrips.api.bucketlist.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;
import com.worldventures.dreamtrips.api.api_common.model.UniqueIdentifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface BucketPhoto extends Identifiable<Integer>, UniqueIdentifiable {
    @SerializedName("url")
    String url();
    @SerializedName("origin_url")
    String originUrl();
}
