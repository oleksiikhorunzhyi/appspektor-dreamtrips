package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.poweron.impl;


import android.animation.AnimatorSet;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.poweron.ForceUpdatePowerOnPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.poweron.ForceUpdatePowerOnScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WizardVideoView;

import java.util.Arrays;

import javax.inject.Inject;

import static android.animation.ObjectAnimator.ofFloat;

public class ForceUpdatePowerOnScreenImpl extends WalletBaseController<ForceUpdatePowerOnScreen, ForceUpdatePowerOnPresenter> implements ForceUpdatePowerOnScreen {

   private static final int SHOW_SOAR_TITLE_DELAY = 1000;
   private static final int CARD_FADE_IN_DELAY = 300;
   private static final int COMMON_FADE_IN_DELAY = 250;

   private TextView walletWizardSplashTitle;
   private Button actionBtn;
   private WizardVideoView wizardVideoView;

   @Inject ForceUpdatePowerOnPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().onBack());
      walletWizardSplashTitle = view.findViewById(R.id.wallet_wizard_power_on_title);
      actionBtn = view.findViewById(R.id.wallet_wizard_power_on_btn);
      wizardVideoView = view.findViewById(R.id.wizard_video_view);
      hideAllView();
      wizardVideoView.setVideoSource(R.raw.wallet_anim_power_on_sc);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_force_fw_update_power_on, viewGroup, false);
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
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      getView().postDelayed(this::startSoarAnimation, SHOW_SOAR_TITLE_DELAY);
   }

   @Override
   public ForceUpdatePowerOnPresenter getPresenter() {
      return presenter;
   }

   private void hideAllView() {
      for (View view : Arrays.asList(actionBtn, walletWizardSplashTitle, wizardVideoView)) {
         view.setAlpha(0);
      }
   }

   private void startSoarAnimation() {
      AnimatorSet mainAnimation = new AnimatorSet();
      mainAnimation
            .play(ofFloat(actionBtn, View.ALPHA, 1).setDuration(COMMON_FADE_IN_DELAY))
            .with(ofFloat(walletWizardSplashTitle, View.ALPHA, 1).setDuration(COMMON_FADE_IN_DELAY))
            .after(ofFloat(wizardVideoView, View.ALPHA, 1).setDuration(CARD_FADE_IN_DELAY));

      mainAnimation.start();
   }


   @Override
   public void setButtonAction(View.OnClickListener onClickListener) {
      actionBtn.setOnClickListener(onClickListener);
   }

   @Override
   public void showDialogEnableBleAndInternet() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_firmware_pre_installation_bluetooth)
            .positiveText(R.string.ok)
            .show();
   }
}