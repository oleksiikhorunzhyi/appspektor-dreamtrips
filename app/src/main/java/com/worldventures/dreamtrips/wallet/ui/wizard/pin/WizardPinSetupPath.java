package com.worldventures.dreamtrips.wallet.ui.wizard.pin;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

@Layout(R.layout.screen_wallet_wizard_pin_setup)
public class WizardPinSetupPath extends StyledPath {

    public final SmartCard smartCard;
    public final Action action;

    public WizardPinSetupPath(SmartCard smartCard) {
        this(smartCard, Action.SETUP);
    }

    public WizardPinSetupPath(SmartCard smartCard, Action action) {
        this.smartCard = smartCard;
        this.action = action;
    }

    @Override
    public MasterDetailPath getMaster() {
        return this;
    }

    public enum Action {
        RESET, SETUP
    }
}
