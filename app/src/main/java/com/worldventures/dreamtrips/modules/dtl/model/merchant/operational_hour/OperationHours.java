package com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.value.Value;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface OperationHours {

   String from();
   String to();
}
