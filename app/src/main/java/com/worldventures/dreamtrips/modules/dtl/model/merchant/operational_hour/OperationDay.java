package com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.value.Value;

import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public abstract class OperationDay {

   public abstract DayOfWeek dayOfWeek();
   public abstract List<OperationHours> operationHours();

   @Value.Derived
   public boolean isHaveOperationHours() {
      return operationHours() != null && !operationHours().isEmpty();
   }

}
