package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.dtl.attributes.model.Attribute;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;


@HttpAction(value = "api/dtl/v2/attributes")
public class CategoryAttributesHttpAction extends AuthorizedHttpAction {

   final @Query("ll") String coordinates;
   final @Query("radius") Double radius;
   final @Query("attribute_type") List<String> attributeTypes;
   final @Query("merchant_type") List<String> merchantTypes;

   @Response List<Attribute> attributes;

   public CategoryAttributesHttpAction(String coordinates, Double radius, List<String> attributeTypes, List<String> keyCategories) {
      this.coordinates = coordinates;
      this.radius = radius;
      this.attributeTypes = attributeTypes;
      this.merchantTypes = keyCategories;
   }

   public List<Attribute> attributes() {
      return attributes;
   }
}
