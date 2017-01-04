package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.RequestSourceTypeAction;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class MerchantsRequestSourceInteractor {

   private final ActionPipe<RequestSourceTypeAction> requestSourceActionPipe;

   public MerchantsRequestSourceInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.requestSourceActionPipe = sessionActionPipeCreator.createPipe(RequestSourceTypeAction.class, Schedulers.immediate());
      this.requestSourceActionPipe.send(RequestSourceTypeAction.list());
   }

   public ActionPipe<RequestSourceTypeAction> requestSourceActionPipe() {
      return requestSourceActionPipe;
   }
}
