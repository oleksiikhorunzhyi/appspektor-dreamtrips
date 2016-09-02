package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.value.Value;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface ThinAttribute {

   String name();
}
