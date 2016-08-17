package com.worldventures.dreamtrips.modules.bucketlist.service.model;

import org.immutables.value.Value;

@Value.Immutable(builder = false)
public interface BucketOrderBody {
   @Value.Parameter
   int position();
}