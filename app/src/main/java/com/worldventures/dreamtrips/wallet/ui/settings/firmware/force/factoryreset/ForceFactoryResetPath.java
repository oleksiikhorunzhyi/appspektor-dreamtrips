package com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.factoryreset;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_force_factory_reset)
public class ForceFactoryResetPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
