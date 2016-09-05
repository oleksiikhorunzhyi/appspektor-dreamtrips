package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import android.text.TextUtils;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public abstract class Currency {

   public abstract String code();

   public abstract String prefix();

   public abstract String suffix();

   public abstract String name();

   @Value.Default public Boolean isDefault() {
      return Boolean.FALSE;
   }

   @Value.Derived
   public String getCurrencyHint() {
      return !TextUtils.isEmpty(suffix()) ? suffix() : code();
   }
}
