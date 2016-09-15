package com.worldventures.dreamtrips.modules.dtl.helper.holder;

import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantByIdCommand;

import java.io.Serializable;

public class MerchantByIdParamsHolder implements Serializable {

   private final String merchantId, offerId;

   public MerchantByIdParamsHolder(String merchantId, String offerId) {
      this.merchantId = merchantId;
      this.offerId = offerId;
   }

   public static MerchantByIdParamsHolder fromAction(MerchantByIdCommand command) {
      return create(command.getMerchantId(), command.getOfferId());
   }

   public static MerchantByIdCommand toAction(MerchantByIdParamsHolder state) {
      return MerchantByIdCommand.create(state.getMerchantId(), state.getOfferId());
   }

   public static MerchantByIdParamsHolder create(String merchantId, String offerId) {
      return new MerchantByIdParamsHolder(merchantId, offerId);
   }

   public String getMerchantId() {
      return merchantId;
   }

   public String getOfferId() {
      return offerId;
   }
}
