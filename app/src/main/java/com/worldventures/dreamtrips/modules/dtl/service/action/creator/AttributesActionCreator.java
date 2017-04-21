package com.worldventures.dreamtrips.modules.dtl.service.action.creator;


import com.worldventures.dreamtrips.api.dtl.attributes.AttributesHttpAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.CategoryAttributesHttpAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.AttributesActionParams;

import java.util.List;

import javax.inject.Inject;

public class AttributesActionCreator implements CategoryHttpActionCreator<CategoryAttributesHttpAction, AttributesActionParams> {

   @Inject public AttributesActionCreator(){}

   @Override
   public CategoryAttributesHttpAction createAction(AttributesActionParams params, List<String> merchantTypes) {
      return new CategoryAttributesHttpAction(params.ll(), params.radius(), params.attributeTypes(), merchantTypes);
   }
}
