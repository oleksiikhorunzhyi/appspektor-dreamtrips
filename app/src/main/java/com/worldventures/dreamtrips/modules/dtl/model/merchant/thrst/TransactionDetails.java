package com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst;

import android.support.annotation.Nullable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.value.Value;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface TransactionDetails extends Serializable {

   @Nullable String transactionId();
   @Nullable Double creditedAmount();
   @Nullable Double currentBalance();
}
