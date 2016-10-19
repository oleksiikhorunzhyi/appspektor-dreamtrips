package com.worldventures.dreamtrips.wallet.ui.settings.firmware.preinstalletion;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_preinstallation)
public class WalletFirmwareChecksPath extends StyledPath {

   private String firmwareFilePath;

   public WalletFirmwareChecksPath(String firmwareFilePath) {
      this.firmwareFilePath = firmwareFilePath;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }

   public String firmwareFilePath() {
      return firmwareFilePath;
   }
}
