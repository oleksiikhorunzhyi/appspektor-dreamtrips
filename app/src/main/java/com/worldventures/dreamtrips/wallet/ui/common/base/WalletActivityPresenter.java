package com.worldventures.dreamtrips.wallet.ui.common.base;

import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;

public class WalletActivityPresenter extends ActivityPresenter<ActivityPresenter.View> {

    private static boolean smart_card_attached;

    public boolean hasSmartCard() {
        return smart_card_attached = !smart_card_attached;
    }
}
