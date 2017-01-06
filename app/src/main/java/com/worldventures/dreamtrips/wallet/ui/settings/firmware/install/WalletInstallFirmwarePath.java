package com.worldventures.dreamtrips.wallet.ui.settings.firmware.install;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

@Layout(R.layout.screen_wallet_install_firmware)
public class WalletInstallFirmwarePath extends StyledPath {

   public final FirmwareUpdateData firmwareData;
   public final SmartCard smartCard;

   public WalletInstallFirmwarePath(SmartCard smartCard, FirmwareUpdateData firmwareData) {
      this.smartCard = smartCard;
      this.firmwareData = firmwareData;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}