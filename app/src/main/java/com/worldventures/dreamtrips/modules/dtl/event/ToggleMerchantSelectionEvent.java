package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

public class ToggleMerchantSelectionEvent {

   private DtlMerchant DtlMerchant;
   private Merchant merchant;

   public ToggleMerchantSelectionEvent(DtlMerchant DtlMerchant) {
      this.DtlMerchant = DtlMerchant;
   }

   public DtlMerchant getDtlMerchant() {
      return DtlMerchant;
   }

   public ToggleMerchantSelectionEvent(Merchant merchant) {
      this.merchant = merchant;
   }

   public Merchant getMerchant() {
      return merchant;
   }
}
