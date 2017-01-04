package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_wizard_pairkey)
public class PairKeyPath extends StyledPath {

   private BarcodeOrigin barcodeOrigin;
   private String barcode;

   public PairKeyPath(BarcodeOrigin scan, String barcode) {
      this.barcodeOrigin = scan;
      this.barcode = barcode;
   }

   public BarcodeOrigin getBarcodeOrigin() {
      return barcodeOrigin;
   }

   public String getBarcode() {
      return barcode;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }

   public enum BarcodeOrigin {
      MANUAL, SCAN
   }
}
