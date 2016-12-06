package com.worldventures.dreamtrips.modules.dtl.service.action.creator;

import com.worldventures.dreamtrips.api.dtl.merchants.MerchantByIdHttpAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.FullMerchantActionParams;

import javax.inject.Inject;

public class FullMerchantActionCreator implements HttpActionCreator<MerchantByIdHttpAction, FullMerchantActionParams> {

   @Inject public FullMerchantActionCreator() {}

   @Override
   public MerchantByIdHttpAction createAction(FullMerchantActionParams params) {
      return new MerchantByIdHttpAction(params.merchantId());
   }
}
