package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import com.worldventures.dreamtrips.api.dtl.attributes.model.AttributeType;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface Attribute {

   Integer id();
   AttributeType type();
   String name();
   String displayName();
   Integer merchantCount();
   Integer partnerCount();
}
