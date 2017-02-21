package com.worldventures.dreamtrips.wallet.ui.wizard.paymentcomplete;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_payment_sync_complete)
public class PaymentSyncFinishPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
