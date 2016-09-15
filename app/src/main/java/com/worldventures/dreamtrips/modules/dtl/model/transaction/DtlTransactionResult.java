package com.worldventures.dreamtrips.modules.dtl.model.transaction;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlTransactionResult {

   String id;
   double creditedAmount;
   double currentBalance;

   public String getId() {
      return id;
   }

   public double getEarnedPoints() {
      return creditedAmount;
   }

   public double getTotal() {
      return currentBalance;
   }

}
