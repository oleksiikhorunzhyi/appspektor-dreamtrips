package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.api.dtl.merchants.EstimatePointsHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.AddRatingHttpAction;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlEarnPointsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class DtlTransactionInteractor {

   private final ActionPipe<EstimatePointsHttpAction> estimatePointsActionPipe;
   private final ActionPipe<AddRatingHttpAction> rateActionPipe;
   private final ActionPipe<DtlEarnPointsAction> earnPointsActionPipe;
   private final ActionPipe<DtlTransactionAction> transactionActionPipe;

   public DtlTransactionInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         SessionActionPipeCreator sessionApiActionPipeCreator) {
      estimatePointsActionPipe = sessionApiActionPipeCreator.createPipe(EstimatePointsHttpAction.class, Schedulers.io());
      rateActionPipe = sessionApiActionPipeCreator.createPipe(AddRatingHttpAction.class, Schedulers.io());
      earnPointsActionPipe = sessionActionPipeCreator.createPipe(DtlEarnPointsAction.class, Schedulers.io());
      transactionActionPipe = sessionActionPipeCreator.createPipe(DtlTransactionAction.class, Schedulers.io());
   }

   public ActionPipe<EstimatePointsHttpAction> estimatePointsActionPipe() {
      return estimatePointsActionPipe;
   }

   public ActionPipe<AddRatingHttpAction> rateActionPipe() {
      return rateActionPipe;
   }

   public ActionPipe<DtlEarnPointsAction> earnPointsActionPipe() {
      return earnPointsActionPipe;
   }

   public ActionPipe<DtlTransactionAction> transactionActionPipe() {
      return transactionActionPipe;
   }
}
