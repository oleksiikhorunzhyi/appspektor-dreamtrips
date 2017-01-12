package com.worldventures.dreamtrips.wallet.ui.settings.firmware.puck_connection;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

@Layout(R.layout.screen_wallet_puck_connection)
public class WalletPuckConnectionPath extends StyledPath {

   public final FirmwareInfo firmwareInfo;
   public final String filePath;
   public final SmartCard smartCard;
   public final FirmwareUpdateData firmwareUpdateData;

   public WalletPuckConnectionPath(SmartCard smartCard, FirmwareUpdateData firmwareUpdateData, FirmwareInfo firmwareInfo, String filePath) {
      this.firmwareInfo = firmwareInfo;
      this.filePath = filePath;
      this.smartCard = smartCard;
      this.firmwareUpdateData = firmwareUpdateData;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
