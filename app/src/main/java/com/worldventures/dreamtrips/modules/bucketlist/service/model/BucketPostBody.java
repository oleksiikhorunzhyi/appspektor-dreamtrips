package com.worldventures.dreamtrips.modules.bucketlist.service.model;

import android.support.annotation.Nullable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Date;
import java.util.List;

@Value.Immutable
@Gson.TypeAdapters
public abstract class BucketPostBody extends BucketBody {
   @Nullable
   public abstract Integer categoryId();

   @Nullable
   public abstract String name();

   @Nullable
   public abstract Date date();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract List<String> tags();

   @Nullable
   public abstract List<String> friends();
}
