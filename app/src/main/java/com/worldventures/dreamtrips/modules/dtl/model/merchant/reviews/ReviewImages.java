package com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews;

import android.support.annotation.Nullable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Gson.TypeAdapters
@Value.Immutable
public interface ReviewImages extends Serializable {

   @Nullable
   String normalUrl();
   @Nullable
   String thumbnailUrl();
}
