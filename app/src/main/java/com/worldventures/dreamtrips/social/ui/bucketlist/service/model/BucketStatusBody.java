package com.worldventures.dreamtrips.social.ui.bucketlist.service.model;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
public interface BucketStatusBody {
   @Value.Parameter
   String status();
}