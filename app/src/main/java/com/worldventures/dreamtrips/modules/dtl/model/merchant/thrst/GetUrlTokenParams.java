package com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst;

import android.support.annotation.Nullable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.value.Value;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface GetUrlTokenParams extends Serializable {

   @Nullable String checkinTime();
   @Nullable String currencyCode();
   @Nullable String receiptPhotoUrl();
   @Nullable LocationThrst location();
}
