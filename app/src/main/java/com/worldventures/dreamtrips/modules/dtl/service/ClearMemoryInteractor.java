package com.worldventures.dreamtrips.modules.dtl.service;


import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.ClearMerchantsStorageAction;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class ClearMemoryInteractor {

   private final ActionPipe<ClearMerchantsStorageAction> clearMerchantsActionActionPipe;

   public ClearMemoryInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.clearMerchantsActionActionPipe = sessionActionPipeCreator.createPipe(ClearMerchantsStorageAction.class, Schedulers.io());
   }

   public void clearMerchantsMemoryCache() {
      clearMerchantsActionActionPipe.send(ClearMerchantsStorageAction.clear());
   }
}
