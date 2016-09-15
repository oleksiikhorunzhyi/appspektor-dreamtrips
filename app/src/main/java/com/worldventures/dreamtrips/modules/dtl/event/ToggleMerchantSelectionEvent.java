package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

public class ToggleMerchantSelectionEvent {
   private DtlMerchant DtlMerchant;

   public ToggleMerchantSelectionEvent(DtlMerchant DtlMerchant) {
      this.DtlMerchant = DtlMerchant;
   }

   public DtlMerchant getDtlMerchant() {
      return DtlMerchant;
   }
}
