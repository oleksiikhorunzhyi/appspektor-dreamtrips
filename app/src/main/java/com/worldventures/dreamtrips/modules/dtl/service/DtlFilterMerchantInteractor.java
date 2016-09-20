package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterMerchantsAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import rx.schedulers.Schedulers;

public class DtlFilterMerchantInteractor {

   private final ActionPipe<DtlFilterDataAction> filterDataPipe;
   private final ActionPipe<DtlFilterMerchantsAction> filterMerchantsPipe;

   public DtlFilterMerchantInteractor(DtlMerchantInteractor dtlMerchantInteractor, DtlLocationInteractor locationInteractor,
         LocationDelegate locationDelegate, SessionActionPipeCreator sessionActionPipeCreator) {
      filterMerchantsPipe = sessionActionPipeCreator.createPipe(DtlFilterMerchantsAction.class, Schedulers.io());
      filterDataPipe = sessionActionPipeCreator.createPipe(DtlFilterDataAction.class, Schedulers.io());

      filterDataPipe.send(DtlFilterDataAction.init());
      filterDataPipe.observeSuccess()
            .subscribe(action -> filterMerchantsPipe.send(new DtlFilterMerchantsAction(action.getResult(),
                  dtlMerchantInteractor.merchantsActionPipe(), locationInteractor.locationPipe(), locationDelegate)));
   }

   public ActionPipe<DtlFilterDataAction> filterDataPipe() {
      return filterDataPipe;
   }

   public ReadActionPipe<DtlFilterMerchantsAction> filterMerchantsActionPipe() {
      return filterMerchantsPipe;
   }
}
