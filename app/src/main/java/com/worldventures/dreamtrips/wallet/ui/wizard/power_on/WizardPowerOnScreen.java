package com.worldventures.dreamtrips.wallet.ui.wizard.power_on;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;

public class WizardPowerOnScreen extends WalletLinearLayout<WizardPowerOnPresenter.Screen, WizardPowerOnPresenter, WizardPowerOnPath> implements WizardPowerOnPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.wallet_wizard_power_on_title) TextView walletWizardSplashTitle;
   @InjectView(R.id.wallet_wizard_power_on_btn) Button actionBtn;

   @InjectView(R.id.card_container) View cardContainer;

   public WizardPowerOnScreen(Context context) {
      super(context);
   }

   public WizardPowerOnScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public WizardPowerOnPresenter createPresenter() {
      return new WizardPowerOnPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> getPresenter().onBack());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public void setButtonAction(OnClickListener onClicklistener) {
      actionBtn.setOnClickListener(onClicklistener);
   }
}
