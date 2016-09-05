package com.worldventures.dreamtrips.wallet.ui.wizard.setup_smartcard;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_wizard_setup_smartcard)
public class WizardSetupSmartCardPath extends StyledPath {

   public final String smartCardId;

   public WizardSetupSmartCardPath(String smartCardId) {
      this.smartCardId = smartCardId;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }

}
