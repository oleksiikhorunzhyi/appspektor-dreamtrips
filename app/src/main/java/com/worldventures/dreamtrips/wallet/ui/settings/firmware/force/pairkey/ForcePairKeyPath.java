package com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.pairkey;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

@Layout(R.layout.screen_wallet_force_fw_update_pairkey)
public class ForcePairKeyPath extends StyledPath {

   private final SmartCard smartCard;
   private final FirmwareUpdateData firmwareUpdateData;

   public ForcePairKeyPath(SmartCard smartCard, FirmwareUpdateData firmwareUpdateData) {
      this.smartCard = smartCard;
      this.firmwareUpdateData = firmwareUpdateData;
   }

   public SmartCard smartCard() {
      return smartCard;
   }

   public FirmwareUpdateData firmwareUpdateData() {
      return firmwareUpdateData;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
