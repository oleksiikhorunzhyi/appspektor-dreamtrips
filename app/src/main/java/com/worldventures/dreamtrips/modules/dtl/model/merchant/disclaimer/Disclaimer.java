package com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.api.dtl.merchants.model.DisclaimerType;

import org.immutables.value.Value;

@SuppressWarnings("unused")
@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface Disclaimer {

   DisclaimerType type();
   String text();
}
