package com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.value.Value;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface OperationHours extends Serializable {

   String from();
   String to();
}
