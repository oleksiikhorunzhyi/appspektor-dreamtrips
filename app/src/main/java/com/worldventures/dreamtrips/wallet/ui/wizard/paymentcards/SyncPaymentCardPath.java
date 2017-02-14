package com.worldventures.dreamtrips.wallet.ui.wizard.paymentcards;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

@Layout(R.layout.screen_wallet_wizard_sync_payment_cards)
public class SyncPaymentCardPath extends StyledPath {

   private final SmartCard smartCard;

   public SyncPaymentCardPath(SmartCard smartCard) {
      this.smartCard = smartCard;
   }

   public SmartCard smartCard() {
      return smartCard;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
