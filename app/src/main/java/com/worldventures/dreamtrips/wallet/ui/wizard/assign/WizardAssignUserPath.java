package com.worldventures.dreamtrips.wallet.ui.wizard.assign;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;

@Layout(R.layout.screen_wallet_wizard_assign_smartcard)
public class WizardAssignUserPath extends StyledPath {

   final ProvisioningMode provisioningMode;

   public WizardAssignUserPath(ProvisioningMode provisioningMode) {
      this.provisioningMode = provisioningMode;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
