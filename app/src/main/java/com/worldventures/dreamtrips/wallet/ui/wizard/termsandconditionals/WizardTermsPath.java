package com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_wizard_termsandconditions)
public class WizardTermsPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
