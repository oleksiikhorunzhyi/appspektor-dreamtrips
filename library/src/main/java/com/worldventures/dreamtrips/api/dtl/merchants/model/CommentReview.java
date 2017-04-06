package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface CommentReview {

   @Nullable @SerializedName("reviewId")
   String reviewId();
   @Nullable @SerializedName("brandId")
   Integer brand();
   @Nullable @SerializedName("userNickName")
   String userNickName();
   @Nullable
   @SerializedName("userImage")
   String userImage();
   @Nullable @SerializedName("reviewText")
   String reviewText();
   @Nullable @SerializedName("rating")
   Integer rating();
   @Nullable @SerializedName("verified")
   Boolean verified();
   @Nullable
   @SerializedName("errors")
   List<Errors> errors();
}
