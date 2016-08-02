package com.worldventures.dreamtrips.wallet.ui.common.base;

import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;

public class WalletActivityPresenter extends ActivityPresenter<ActivityPresenter.View> {

    public boolean hasSmartCard() {
        return false;
    }
}
