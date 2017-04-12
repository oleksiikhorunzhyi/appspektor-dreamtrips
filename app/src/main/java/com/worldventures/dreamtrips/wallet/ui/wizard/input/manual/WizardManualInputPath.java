package com.worldventures.dreamtrips.wallet.ui.wizard.input.manual;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_wizard_manual_input)
public class WizardManualInputPath extends StyledPath {
   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}