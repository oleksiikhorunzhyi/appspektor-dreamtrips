package com.worldventures.dreamtrips.wallet.ui;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.wallet.ui.presenter.WalletActivityPresenter;

import flow.History;

public class WalletActivity extends FlowActivity<WalletActivityPresenter> {
    @Override
    protected ComponentDescription getCurrentComponent() {
        return null;
    }

    @Override
    protected History provideDefaultHistory() {
        return null;
    }

    @Override
    protected WalletActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return null;
    }
}
