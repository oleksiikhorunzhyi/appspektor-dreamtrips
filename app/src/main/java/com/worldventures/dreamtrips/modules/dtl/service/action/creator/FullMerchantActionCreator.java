package com.worldventures.dreamtrips.modules.dtl.service.action.creator;

import com.worldventures.dreamtrips.api.dtl.merchants.GetMerchantByIdHttpAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.FullMerchantActionParams;

public class FullMerchantActionCreator implements HttpActionCreator<GetMerchantByIdHttpAction, FullMerchantActionParams> {

   @Override
   public GetMerchantByIdHttpAction createAction(FullMerchantActionParams params) {
      return new GetMerchantByIdHttpAction(params.merchantId());
   }
}
