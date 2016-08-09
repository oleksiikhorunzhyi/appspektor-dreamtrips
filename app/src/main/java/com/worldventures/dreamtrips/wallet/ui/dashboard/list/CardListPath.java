package com.worldventures.dreamtrips.wallet.ui.dashboard.list;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_cardlist)
public class CardListPath extends StyledPath {

    @Override
    public MasterDetailPath getMaster() {
        return this;
    }
}