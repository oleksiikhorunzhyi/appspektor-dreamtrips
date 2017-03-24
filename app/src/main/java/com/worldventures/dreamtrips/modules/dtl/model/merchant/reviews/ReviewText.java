package com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface ReviewText extends Serializable {
   @Nullable
   @SerializedName("Field")
   String field();

   @Nullable
   @SerializedName("Message")
   String message();

   @Nullable
   @SerializedName("Code")
   String code();
}
