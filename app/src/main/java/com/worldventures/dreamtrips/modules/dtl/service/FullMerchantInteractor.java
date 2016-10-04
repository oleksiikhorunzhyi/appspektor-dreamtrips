package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.FullMerchantAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import rx.schedulers.Schedulers;

public class FullMerchantInteractor {

   private final ActionPipe<FullMerchantAction> fullMerchantPipe;

   public FullMerchantInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      fullMerchantPipe = sessionActionPipeCreator.createPipe(FullMerchantAction.class, Schedulers.io());
   }

   public ReadActionPipe<FullMerchantAction> fullMerchantPipe() {
      return fullMerchantPipe.asReadOnly();
   }

   public void load(FullMerchantAction command) {
      fullMerchantPipe.send(command);
   }
}
