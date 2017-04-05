package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.ui.wizard.ProvisioningMode;

@Layout(R.layout.screen_wallet_wizard_pairkey)
public class PairKeyPath extends StyledPath {

   public final ProvisioningMode mode;
   public final String barcode;

   public PairKeyPath(ProvisioningMode mode, String barcode) {
      this.mode = mode;
      this.barcode = barcode;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
