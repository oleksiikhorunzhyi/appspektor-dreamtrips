package com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.poweron;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_force_fw_update_power_on)
public class ForceUpdatePowerOnPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
