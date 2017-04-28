package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_preinstallation)
public class WalletFirmwareChecksPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
