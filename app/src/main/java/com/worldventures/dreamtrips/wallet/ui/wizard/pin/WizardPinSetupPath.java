package com.worldventures.dreamtrips.wallet.ui.wizard.pin;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_wizard_pin_setup)
public class WizardPinSetupPath extends StyledPath {
    @Override
    public MasterDetailPath getMaster() {
        return this;
    }
}
