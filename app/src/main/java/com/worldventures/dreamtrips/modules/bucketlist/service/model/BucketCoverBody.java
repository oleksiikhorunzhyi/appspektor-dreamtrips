package com.worldventures.dreamtrips.modules.bucketlist.service.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
public abstract class BucketCoverBody extends BucketBody {
   @SerializedName("cover_photo_id")
   public abstract String coverId();
}
