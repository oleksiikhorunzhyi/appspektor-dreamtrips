package com.worldventures.dreamtrips.wallet.ui.wizard.card_alias;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_wizard_card_alias)
public class WizardCardNamePath extends StyledPath {

    @Override
    public MasterDetailPath getMaster() {
        return this;
    }
}
