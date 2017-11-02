package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.core.model.ImagePathHolder;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Gson.TypeAdapters
@Value.Immutable
public interface MerchantMedia extends ImagePathHolder  {

   String category();
   Integer width();
   Integer height();
}