package com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface FormErrors extends Serializable {

   @Nullable
   FieldErrors fieldErrors();
}
