package com.worldventures.dreamtrips.wallet.ui.wizard.card_alias;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_wizard_card_alias)
public class WizardCardNamePath extends StyledPath {

    private final String smartCardId;

    public WizardCardNamePath(String smartCardId) {
        this.smartCardId = smartCardId;
    }

    @Override
    public MasterDetailPath getMaster() {
        return this;
    }

    public String getSmartCardId() {
        return smartCardId;
    }
}
