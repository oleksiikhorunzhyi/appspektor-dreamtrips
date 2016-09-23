package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;

public class ToggleMerchantSelectionEvent {

   private ThinMerchant thinMerchant;
   private Merchant merchant;

   public ToggleMerchantSelectionEvent(ThinMerchant merchant) {
      this.thinMerchant = merchant;
   }

   public ThinMerchant getThinMerchant() {
      return thinMerchant;
   }

   public ToggleMerchantSelectionEvent(Merchant merchant) {
      this.merchant = merchant;
   }

   public Merchant getMerchant() {
      return merchant;
   }
}
