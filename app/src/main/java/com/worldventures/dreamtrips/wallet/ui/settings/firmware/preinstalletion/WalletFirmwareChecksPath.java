package com.worldventures.dreamtrips.wallet.ui.settings.firmware.preinstalletion;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

@Layout(R.layout.screen_wallet_preinstallation)
public class WalletFirmwareChecksPath extends StyledPath {

   public final String firmwareFilePath;
   public final FirmwareInfo firmwareInfo;
   public final SmartCard smartCard;
   public final FirmwareUpdateData firmwareUpdateData;

   public WalletFirmwareChecksPath(SmartCard smartCard, FirmwareUpdateData firmwareUpdateData, String firmwareFilePath, FirmwareInfo firmwareInfo) {
      this.firmwareFilePath = firmwareFilePath;
      this.firmwareInfo = firmwareInfo;
      this.smartCard = smartCard;
      this.firmwareUpdateData = firmwareUpdateData;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
