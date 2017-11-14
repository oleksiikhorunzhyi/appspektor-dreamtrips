package com.worldventures.dreamtrips.social.ui.bucketlist.service.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

@Value.Immutable
public abstract class BucketCoverBody extends BucketBody {
   @SerializedName("cover_photo_id")
   public abstract String coverId();
}
