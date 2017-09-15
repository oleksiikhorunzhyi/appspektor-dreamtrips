package com.worldventures.dreamtrips.social.ui.bucketlist.service.model;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

import org.immutables.value.Value;

public abstract class BucketBody {
   @Nullable
   public abstract String id();

   @Nullable
   public abstract String type();

   @Nullable
   @Value.Default
   public String status() {
      return BucketItem.NEW;
   }
}