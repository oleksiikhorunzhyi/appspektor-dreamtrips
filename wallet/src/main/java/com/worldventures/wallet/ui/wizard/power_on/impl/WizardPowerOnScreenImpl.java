package com.worldventures.wallet.ui.wizard.power_on.impl;


import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.widget.WizardVideoView;
import com.worldventures.wallet.ui.wizard.power_on.WizardPowerOnPresenter;
import com.worldventures.wallet.ui.wizard.power_on.WizardPowerOnScreen;
import com.worldventures.wallet.ui.wizard.welcome.WizardWelcomeScreen;

import javax.inject.Inject;

public class WizardPowerOnScreenImpl extends WalletBaseController<WizardWelcomeScreen, WizardPowerOnPresenter> implements WizardPowerOnScreen {

   private Button actionBtn;

   @Inject WizardPowerOnPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().onBack());
      final WizardVideoView wizardVideoView = view.findViewById(R.id.wizard_video_view);
      wizardVideoView.setVideoSource(R.raw.wallet_anim_power_on_sc);
      actionBtn = view.findViewById(R.id.wallet_wizard_power_on_btn);
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
            : button -> getPresenter().openCheckScreen());
   }

   @Override
   public WizardPowerOnPresenter getPresenter() {
      return presenter;
   }
}
