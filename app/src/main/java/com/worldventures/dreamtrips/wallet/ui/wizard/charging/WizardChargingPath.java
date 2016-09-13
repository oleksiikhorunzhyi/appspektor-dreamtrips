package com.worldventures.dreamtrips.wallet.ui.wizard.charging;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.wallet_wizard_charging_screen)
public class WizardChargingPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
