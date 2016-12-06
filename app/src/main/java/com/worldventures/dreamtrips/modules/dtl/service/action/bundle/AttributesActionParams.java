package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import com.worldventures.dreamtrips.api.dtl.attributes.model.AttributeType;

import org.immutables.value.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Value.Immutable
public abstract class AttributesActionParams implements HttpActionParams {

   public abstract String ll();

   public abstract double radius();

   @Value.Default public List<String> attributeTypes() {
      return Arrays.asList(AttributeType.AMENITY.toString().toLowerCase(Locale.US));
   }
}
