package com.worldventures.dreamtrips.modules.bucketlist.service.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Gson.TypeAdapters
public abstract class BucketPostBody extends BucketBody {
   @Nullable
   @SerializedName("category_id")
   public abstract Integer categoryId();

   @Nullable
   public abstract String name();

   @Nullable
   @SerializedName("target_date")
   public abstract String date();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract List<String> tags();

   @Nullable
   public abstract List<String> friends();
}
