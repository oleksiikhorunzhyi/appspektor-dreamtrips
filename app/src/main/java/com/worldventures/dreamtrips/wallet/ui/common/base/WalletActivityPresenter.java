package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;

public class WalletActivityPresenter extends ActivityPresenter<ActivityPresenter.View> {

    public static boolean hasSmartCard = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasSmartCard = !hasSmartCard;
    }

    public boolean hasSmartCard() {
        return hasSmartCard;
    }
}
