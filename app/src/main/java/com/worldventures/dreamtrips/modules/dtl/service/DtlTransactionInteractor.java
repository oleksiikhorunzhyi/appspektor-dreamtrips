package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.api.dtl.merchants.EstimationHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.RatingHttpAction;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlEarnPointsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class DtlTransactionInteractor {

   private final ActionPipe<EstimationHttpAction> estimatePointsActionPipe;
   private final ActionPipe<RatingHttpAction> rateActionPipe;
   private final ActionPipe<DtlEarnPointsAction> earnPointsActionPipe;
   private final ActionPipe<DtlTransactionAction> transactionActionPipe;

   public DtlTransactionInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         SessionActionPipeCreator sessionApiActionPipeCreator) {
      estimatePointsActionPipe = sessionApiActionPipeCreator.createPipe(EstimationHttpAction.class, Schedulers.io());
      rateActionPipe = sessionApiActionPipeCreator.createPipe(RatingHttpAction.class, Schedulers.io());
      earnPointsActionPipe = sessionActionPipeCreator.createPipe(DtlEarnPointsAction.class, Schedulers.io());
      transactionActionPipe = sessionActionPipeCreator.createPipe(DtlTransactionAction.class, Schedulers.io());
   }

   public ActionPipe<EstimationHttpAction> estimatePointsActionPipe() {
      return estimatePointsActionPipe;
   }

   public ActionPipe<RatingHttpAction> rateActionPipe() {
      return rateActionPipe;
   }

   public ActionPipe<DtlEarnPointsAction> earnPointsActionPipe() {
      return earnPointsActionPipe;
   }

   public ActionPipe<DtlTransactionAction> transactionActionPipe() {
      return transactionActionPipe;
   }
}
