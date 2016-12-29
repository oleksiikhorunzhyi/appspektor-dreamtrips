package com.worldventures.dreamtrips.modules.dtl.service.action.creator;


import com.worldventures.dreamtrips.api.dtl.attributes.AttributesHttpAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.AttributesActionParams;

import javax.inject.Inject;

public class AttributesActionCreator implements HttpActionCreator<AttributesHttpAction, AttributesActionParams> {

   @Inject public AttributesActionCreator(){}

   @Override
   public AttributesHttpAction createAction(AttributesActionParams params) {
      return new AttributesHttpAction(params.ll(), params.radius(), params.attributeTypes());
   }
}
