package com.worldventures.dreamtrips.wallet.ui.settings.firmware.preinstalletion;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareInfo;

@Layout(R.layout.screen_wallet_preinstallation)
public class WalletFirmwareChecksPath extends StyledPath {

   public final String firmwareFilePath;
   public final FirmwareInfo firmwareInfo;

   public WalletFirmwareChecksPath(String firmwareFilePath, FirmwareInfo firmwareInfo) {
      this.firmwareFilePath = firmwareFilePath;
      this.firmwareInfo = firmwareInfo;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
