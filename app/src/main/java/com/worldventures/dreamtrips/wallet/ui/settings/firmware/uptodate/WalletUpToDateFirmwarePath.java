package com.worldventures.dreamtrips.wallet.ui.settings.firmware.uptodate;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_up_to_date_firmware)
public class WalletUpToDateFirmwarePath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}