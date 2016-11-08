package com.worldventures.dreamtrips.wallet.ui.settings.firmware.install;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;

@Layout(R.layout.screen_wallet_install_firmware)
public class WalletInstallFirmwarePath extends StyledPath {

   public final FirmwareUpdateData firmwareData;

   public WalletInstallFirmwarePath(FirmwareUpdateData firmwareData) {
      this.firmwareData = firmwareData;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}