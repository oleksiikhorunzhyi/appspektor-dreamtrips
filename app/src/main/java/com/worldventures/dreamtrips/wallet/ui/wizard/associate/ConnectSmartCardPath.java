package com.worldventures.dreamtrips.wallet.ui.wizard.associate;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_connect_smartcard)
public class ConnectSmartCardPath extends StyledPath {

   public final String barcode;

   public ConnectSmartCardPath(String barcode) {
      this.barcode = barcode;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
