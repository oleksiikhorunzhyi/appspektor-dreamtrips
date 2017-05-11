package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.helper.holder.FullMerchantParamsHolder;
import com.worldventures.dreamtrips.modules.dtl.service.action.FullMerchantAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.FullMerchantActionParams;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.ReadActionPipe;
import rx.schedulers.Schedulers;

public class FullMerchantInteractor {

   private final ActionPipe<FullMerchantAction> fullMerchantPipe;
   private final DtlLocationInteractor dtlLocationInteractor;

   public FullMerchantInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         DtlLocationInteractor dtlLocationInteractor) {
      fullMerchantPipe = sessionActionPipeCreator.createPipe(FullMerchantAction.class, Schedulers.io());

      this.dtlLocationInteractor = dtlLocationInteractor;
   }

   public ReadActionPipe<FullMerchantAction> fullMerchantPipe() {
      return fullMerchantPipe.asReadOnly();
   }

   public void load(String merchantId, boolean fromRating) {
      load(merchantId, null, fromRating);
   }

   public void load(FullMerchantParamsHolder fullMerchantParamsHolder) {
      load(fullMerchantParamsHolder.getMerchantId(), fullMerchantParamsHolder.getOfferId(), false);
   }

   public void load(String merchantId, String offerId, boolean fromRating) {
      dtlLocationInteractor.locationSourcePipe().observeSuccessWithReplay()
            .take(1)
            .map(Command::getResult)
            .map(location -> FullMerchantAction.create(merchantId, offerId, location, fromRating))
            .subscribe(fullMerchantPipe::send);
   }
}
