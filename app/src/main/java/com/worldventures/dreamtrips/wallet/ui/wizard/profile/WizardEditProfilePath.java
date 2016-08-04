package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_wizard_personal_info)
public class WizardEditProfilePath extends StyledPath {

    private final String smartCardId;

    public WizardEditProfilePath(String smartCardId) {
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
