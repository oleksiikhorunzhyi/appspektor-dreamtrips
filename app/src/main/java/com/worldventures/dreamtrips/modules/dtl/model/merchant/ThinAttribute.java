package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.value.Value;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface ThinAttribute extends Serializable {

   String name();
}
