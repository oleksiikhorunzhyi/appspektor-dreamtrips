package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import android.text.TextUtils;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Gson.TypeAdapters
@Value.Immutable
public abstract class Currency implements Serializable {

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
