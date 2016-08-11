package com.worldventures.dreamtrips.wallet.ui.settings;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

@Layout(R.layout.screen_wallet_settings)
public class WalletCardSettingsPath extends StyledPath {

    private final SmartCard smartCard;

    public WalletCardSettingsPath(@NonNull SmartCard smartCard) {
        this.smartCard = smartCard;
    }

    @Override
    public MasterDetailPath getMaster() {
        return this;
    }

    public SmartCard getSmartCard() {
        return smartCard;
    }

    @Override
    public boolean equals(Object o) {
        // for back navigation
        return o != null && getClass().equals(o.getClass());
    }
}
