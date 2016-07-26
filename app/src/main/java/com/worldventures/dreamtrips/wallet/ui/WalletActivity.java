package com.worldventures.dreamtrips.wallet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.wallet.ui.presenter.WalletActivityPresenter;
import com.worldventures.dreamtrips.wallet.ui.view.wizard.WizardSplashPath;

import flow.History;

import static com.worldventures.dreamtrips.wallet.di.WalletActivityModule.WALLET;

public class WalletActivity extends FlowActivity<WalletActivityPresenter> {
    @Override
    protected ComponentDescription getCurrentComponent() {
        return rootComponentsProvider.getComponentByKey(WALLET);
    }

    @Override
    protected History provideDefaultHistory() {
        return History.single(getPresentationModel().hasSmartCard() ? null : new WizardSplashPath());
    }

    @Override
    protected WalletActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new WalletActivityPresenter();
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, WalletActivity.class));
    }
}
