package com.worldventures.dreamtrips.wallet.ui.settings.firmware.install;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareInfo;

@Layout(R.layout.screen_wallet_install_firmware)
public class WalletInstallFirmwarePath extends StyledPath {

   public final String filePath;
   public final FirmwareInfo firmwareInfo;

   public WalletInstallFirmwarePath(String filePath, FirmwareInfo firmwareInfo) {
      this.filePath = filePath;
      this.firmwareInfo = firmwareInfo;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}