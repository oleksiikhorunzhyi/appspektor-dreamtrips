package com.worldventures.dreamtrips.modules.dtl.service.action.creator;


import com.worldventures.dreamtrips.modules.dtl.service.action.CategoryAttributesHttpAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.AttributesActionParams;

import java.util.List;

public class AttributesActionCreator implements CategoryHttpActionCreator<CategoryAttributesHttpAction, AttributesActionParams> {

   @Override
   public CategoryAttributesHttpAction createAction(AttributesActionParams params, List<String> merchantTypes) {
      return new CategoryAttributesHttpAction(params.ll(), params.radius(), params.attributeTypes(), merchantTypes);
   }
}
