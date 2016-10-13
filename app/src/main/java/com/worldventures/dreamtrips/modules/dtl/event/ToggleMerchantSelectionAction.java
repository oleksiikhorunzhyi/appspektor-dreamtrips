package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ToggleMerchantSelectionAction extends ValueCommandAction<ThinMerchant> {

   public static ToggleMerchantSelectionAction clear() {
      return new ToggleMerchantSelectionAction(null);
   }

   public static ToggleMerchantSelectionAction select(ThinMerchant merchant) {
      return new ToggleMerchantSelectionAction(merchant);
   }

   public ToggleMerchantSelectionAction(ThinMerchant merchant) {
      super(merchant);
   }

   public boolean isClearSelection() {
      return getResult() == null;
   }
}
