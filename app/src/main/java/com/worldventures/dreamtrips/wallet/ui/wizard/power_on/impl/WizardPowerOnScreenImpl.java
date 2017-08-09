package com.worldventures.dreamtrips.wallet.ui.wizard.power_on.impl;


import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.widget.WizardVideoView;
import com.worldventures.dreamtrips.wallet.ui.wizard.power_on.WizardPowerOnPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.power_on.WizardPowerOnScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomeScreen;

import javax.inject.Inject;

import butterknife.InjectView;

public class WizardPowerOnScreenImpl extends WalletBaseController<WizardWelcomeScreen, WizardPowerOnPresenter> implements WizardPowerOnScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.wallet_wizard_power_on_title) TextView walletWizardSplashTitle;
   @InjectView(R.id.wallet_wizard_power_on_btn) Button actionBtn;
   @InjectView(R.id.wizard_video_view) WizardVideoView wizardVideoView;

   @Inject WizardPowerOnPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().onBack());
      wizardVideoView.setVideoSource(R.raw.wallet_anim_power_on_sc);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_power_on, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @Override
   public void setButtonAction(boolean isReadyToContinue) {
      actionBtn.setOnClickListener(isReadyToContinue
            ? button -> getPresenter().openUserAgreement()
            : button ->  getPresenter().openCheckScreen());
   }

   @Override
   public WizardPowerOnPresenter getPresenter() {
      return presenter;
   }
}
