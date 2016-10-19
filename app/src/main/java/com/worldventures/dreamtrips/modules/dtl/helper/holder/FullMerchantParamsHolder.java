package com.worldventures.dreamtrips.modules.dtl.helper.holder;

import com.worldventures.dreamtrips.modules.dtl.service.action.FullMerchantAction;

import java.io.Serializable;

public class FullMerchantParamsHolder implements Serializable {

   private final String merchantId, offerId;

   public FullMerchantParamsHolder(String merchantId, String offerId) {
      this.merchantId = merchantId;
      this.offerId = offerId;
   }

   public static FullMerchantParamsHolder fromAction(FullMerchantAction command) {
      return create(command.getMerchantId(), command.getOfferId());
   }

   public static FullMerchantParamsHolder create(String merchantId, String offerId) {
      return new FullMerchantParamsHolder(merchantId, offerId);
   }

   public String getMerchantId() {
      return merchantId;
   }

   public String getOfferId() {
      return offerId;
   }
}
