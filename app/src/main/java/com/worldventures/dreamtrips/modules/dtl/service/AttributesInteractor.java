package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.modules.dtl.service.action.AttributesAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class AttributesInteractor {

   private final ActionPipe<AttributesAction> attributesPipe;

   public AttributesInteractor(Janet janet) {
      attributesPipe = janet.createPipe(AttributesAction.class, Schedulers.io());
   }

   public ActionPipe<AttributesAction> attributesPipe() {
      return attributesPipe;
   }
}
