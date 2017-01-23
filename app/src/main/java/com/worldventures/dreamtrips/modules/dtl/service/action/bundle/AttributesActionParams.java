package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import com.worldventures.dreamtrips.api.dtl.attributes.model.AttributeType;

import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Value.Immutable
public abstract class AttributesActionParams implements HttpActionParams {

   private static final String RESTAURANT = "restaurant";
   private static final String BAR = "bar";
   private List<String> merchantTypeList = new ArrayList<>();

   public abstract String ll();

   public abstract double radius();

   public AttributesActionParams() {
      merchantTypeList.add(RESTAURANT);
      merchantTypeList.add(BAR);
   }

   @Value.Default public List<String> attributeTypes() {
      return Arrays.asList(AttributeType.AMENITY.toString().toLowerCase(Locale.US));
   }

   @Value.Default
   public List<String> getMerchantType() {
      return merchantTypeList;
   }

   public void setMerchantType(List<String> merchantTypeList) {
      this.merchantTypeList = merchantTypeList;
   }
}
