package com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.value.Value;

import java.io.Serializable;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface FlaggingReviewParams extends Serializable {

   List<Errors> errors();
}
