package com.worldventures.dreamtrips.wallet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.wallet.di.WalletActivityModule;
import com.worldventures.dreamtrips.wallet.ui.presenter.WalletActivityPresenter;
import com.worldventures.dreamtrips.wallet.ui.view.wizard.WizardSplashPath;

import flow.History;

import static com.worldventures.dreamtrips.wallet.di.WalletActivityModule.WALLET;

@Layout(R.layout.activity_wallet)
public class WalletActivity extends FlowActivity<WalletActivityPresenter> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navigationDrawerPresenter.setCurrentComponent(rootComponentsProvider
                .getComponentByKey(WalletActivityModule.WALLET));
    }

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

    public static void startWallet(Context context) {
        context.startActivity(new Intent(context, WalletActivity.class));
    }
}
