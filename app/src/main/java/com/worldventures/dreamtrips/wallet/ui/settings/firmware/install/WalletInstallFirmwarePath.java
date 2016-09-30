package com.worldventures.dreamtrips.wallet.ui.settings.firmware.install;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_install_firmware)
public class WalletInstallFirmwarePath extends StyledPath {

   private final String filePath;

   public WalletInstallFirmwarePath(String filePath) {
      this.filePath = filePath;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }

   public String filePath() {
      return filePath;
   }
}