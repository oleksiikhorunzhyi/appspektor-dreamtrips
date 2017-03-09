package com.worldventures.dreamtrips.wallet.ui.settings.firmware.installsuccess;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;

@Layout(R.layout.screen_wallet_success_install_firmware)
public class WalletSuccessInstallFirmwarePath extends StyledPath {

   public final FirmwareUpdateData firmwareUpdateData;

   public WalletSuccessInstallFirmwarePath(FirmwareUpdateData firmwareUpdateData) {
      this.firmwareUpdateData = firmwareUpdateData;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}