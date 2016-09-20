package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.AttributesAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class AttributesInteractor {

   private final ActionPipe<AttributesAction> attributesPipe;

   public AttributesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      attributesPipe = sessionActionPipeCreator.createPipe(AttributesAction.class, Schedulers.io());
   }

   public ActionPipe<AttributesAction> attributesPipe() {
      return attributesPipe;
   }
}
