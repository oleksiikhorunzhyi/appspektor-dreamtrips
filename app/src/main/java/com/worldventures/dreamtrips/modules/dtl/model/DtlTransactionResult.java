package com.worldventures.dreamtrips.modules.dtl.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlTransactionResult {
    double earnedPoints;
    double total;

    public double getEarnedPoints() {
        return earnedPoints;
    }

    public double getTotal() {
        return total;
    }

}
