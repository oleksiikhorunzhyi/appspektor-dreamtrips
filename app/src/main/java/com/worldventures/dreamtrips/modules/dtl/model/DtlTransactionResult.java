package com.worldventures.dreamtrips.modules.dtl.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlTransactionResult {

    String transactionId;
    double creditedAmount;
    double currentBalance;

    public String getTransactionId() {
        return transactionId;
    }

    public double getEarnedPoints() {
        return creditedAmount;
    }

    public double getTotal() {
        return currentBalance;
    }

}
