package com.worldventures.dreamtrips.modules.dtl.service.action.creator;

import com.worldventures.dreamtrips.api.dtl.merchants.GetTransactionRequestPilotAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.TransactionThrstActionParams;

import javax.inject.Inject;


public class TransactionThrstCreator implements HttpActionCreator<GetTransactionRequestPilotAction, TransactionThrstActionParams> {

   @Inject
   public TransactionThrstCreator(){}

   @Override
   public GetTransactionRequestPilotAction createAction(TransactionThrstActionParams params) {
      return new GetTransactionRequestPilotAction(params.merchantId(),
                                          params.transactionId());
   }
}
