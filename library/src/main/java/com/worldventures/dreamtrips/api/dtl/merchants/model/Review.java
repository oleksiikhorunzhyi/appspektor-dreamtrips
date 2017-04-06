package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface Review {

   @Nullable @SerializedName("lastModeratedTimeUtc")
   String lastModeratedTimeUtc();
   @Nullable @SerializedName("reviewId")
   String reviewId();
   @Nullable @SerializedName("brandId")
   Integer brand();
   @Nullable @SerializedName("userNickName")
   String userNickName();
   @Nullable
   @SerializedName("userImage")
   UserImage userImage();
   @Nullable @SerializedName("reviewText")
   String reviewText();
   @Nullable @SerializedName("rating")
   Integer rating();
   @Nullable @SerializedName("verified")
   Boolean verified();
   @Nullable @SerializedName("userId")
   String userId();
   @Nullable
   @SerializedName("errors")
   List<Errors> errors();

   @Nullable @SerializedName("photos")
   List<ReviewImages> photos();
}
