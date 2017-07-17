package com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst;

import android.support.annotation.Nullable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.value.Value;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface ThrstInfo extends Serializable {

   @Nullable String RedirectUrl();
   @Nullable String Token();
}
