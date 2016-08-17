package com.worldventures.dreamtrips.wallet.ui.wizard.splash;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class WizardSplashScreen extends WalletFrameLayout<WizardSplashScreenPresenter.Screen, WizardSplashScreenPresenter, WizardSplashPath> implements WizardSplashScreenPresenter.Screen {
   @InjectView(R.id.wallet_wizard_splash_title) TextView title;

   public WizardSplashScreen(Context context) {
      super(context);
   }

   public WizardSplashScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public WizardSplashScreenPresenter createPresenter() {
      return new WizardSplashScreenPresenter(getContext(), getInjector());
   }

   @OnClick(R.id.wallet_wizard_splash_btn)
   void onStartScanCardClicked() {
      getPresenter().startScanCard();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }
}
