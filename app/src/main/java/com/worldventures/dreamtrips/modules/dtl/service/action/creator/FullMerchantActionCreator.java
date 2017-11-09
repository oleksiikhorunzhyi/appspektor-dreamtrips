package com.worldventures.dreamtrips.modules.dtl.service.action.creator;

import com.worldventures.dreamtrips.api.dtl.merchants.GetMerchantByIdHttpAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.FullMerchantActionParams;

import javax.inject.Inject;

public class FullMerchantActionCreator implements HttpActionCreator<GetMerchantByIdHttpAction, FullMerchantActionParams> {

   @Inject
   public FullMerchantActionCreator() {
      //do nothing
   }

   @Override
   public GetMerchantByIdHttpAction createAction(FullMerchantActionParams params) {
      return new GetMerchantByIdHttpAction(params.merchantId());
   }
}
