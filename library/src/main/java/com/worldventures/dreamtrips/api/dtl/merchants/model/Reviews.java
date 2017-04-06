package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;
import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface Reviews {

   @SerializedName("total")
   String total();
   @SerializedName("ratingAverage")
   String ratingAverage();
   @Nullable
   @SerializedName("reviews")
   List<Review> reviews();
   @Nullable
   @SerializedName("reviewSettings")
   ReviewSettings reviewSettings();
}
