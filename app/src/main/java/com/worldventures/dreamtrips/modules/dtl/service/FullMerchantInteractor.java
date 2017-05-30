package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.helper.holder.FullMerchantParamsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewSummary;
import com.worldventures.dreamtrips.modules.dtl.service.action.FullMerchantAction;

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

   public void load(String merchantId, ReviewSummary review, boolean fromRating) {
      load(merchantId, review, null, fromRating);
   }

   public void load(FullMerchantParamsHolder fullMerchantParamsHolder) {
      load(fullMerchantParamsHolder.getMerchantId(), null, fullMerchantParamsHolder.getOfferId(), false);
   }

   public void load(String merchantId, ReviewSummary review, String offerId, boolean fromRating) {
      dtlLocationInteractor.locationSourcePipe().observeSuccessWithReplay()
            .take(1)
            .map(Command::getResult)
            .map(location -> FullMerchantAction.create(merchantId, offerId, location, fromRating, review))
            .subscribe(fullMerchantPipe::send);
   }
}
