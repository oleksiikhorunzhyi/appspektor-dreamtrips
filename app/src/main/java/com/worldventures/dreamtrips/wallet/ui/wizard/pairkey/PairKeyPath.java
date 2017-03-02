package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_wizard_pairkey)
public class PairKeyPath extends StyledPath {

   private String barcode;

   public PairKeyPath(String barcode) {
      this.barcode = barcode;
   }

   public String getBarcode() {
      return barcode;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
