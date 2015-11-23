package com.worldventures.dreamtrips.modules.dtl.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlTransactionResult {
    double creditedAmount;
    double currentBalance;

    public double getEarnedPoints() {
        return creditedAmount;
    }

    public double getTotal() {
        return currentBalance;
    }

}
