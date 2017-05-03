package com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

@Layout(R.layout.screen_wallet_new_firmware_available)
public class WalletNewFirmwareAvailablePath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}