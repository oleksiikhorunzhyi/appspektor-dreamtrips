package com.worldventures.dreamtrips.wallet.ui.view.wizard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.presenter.WizardSplashScreenPresenter;
import com.worldventures.dreamtrips.wallet.ui.presenter.WizardSplashScreenPresenterImpl;

import butterknife.InjectView;
import butterknife.OnClick;

public class WizardSplashScreenImpl extends WalletLinearLayout<WizardSplashScreen, WizardSplashScreenPresenter, WizardSplashPath>
        implements WizardSplashScreen {
    @InjectView(R.id.wallet_wizard_splash_title)
    TextView title;

    public WizardSplashScreenImpl(Context context) {
        super(context);
    }

    public WizardSplashScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public WizardSplashScreenPresenter createPresenter() {
        return new WizardSplashScreenPresenterImpl(getContext(), getInjector());
    }

    @OnClick(R.id.wallet_wizard_splash_btn)
    void onStartScanCardClicked() {
        getPresenter().startScanCard();
    }
}
