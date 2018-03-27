package com.worldventures.wallet.ui.wizard.splash.impl;

import android.animation.AnimatorSet;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.widget.anim.FlipAnim;
import com.worldventures.wallet.ui.wizard.splash.WizardSplashPresenter;
import com.worldventures.wallet.ui.wizard.splash.WizardSplashScreen;

import javax.inject.Inject;

import static android.animation.ObjectAnimator.ofFloat;

public class WizardSplashScreenImpl extends WalletBaseController<WizardSplashScreen, WizardSplashPresenter> implements WizardSplashScreen {

   private static final String KEY_STATE_FADE_IN_ANIMATION_STATE = "WizardSplashScreenImpl#KEY_STATE_FADE_IN_ANIMATION_STATE";

   private static final int FLIP_ANIM_DELAY = 500;
   private static final int CARD_FADE_IN_DURATION = 300;
   private static final int COMMON_FADE_IN_DURATION = 250;

   @Inject WizardSplashPresenter presenter;

   private TextView walletWizardSplashTitle;
   private Button actionBtn;
   private View cardContainer;
   private View frontView;
   private View backView;
   private FlipAnim flipAnim;

   private boolean fadeInAnimationFinished;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().onBack());
      walletWizardSplashTitle = view.findViewById(R.id.wallet_wizard_splash_title);
      actionBtn = view.findViewById(R.id.wallet_wizard_splash_btn);
      actionBtn.setOnClickListener(v -> getPresenter().startScanCard());
      cardContainer = view.findViewById(R.id.card_container);
      frontView = view.findViewById(R.id.wallet_wizard_smarcard_front);
      backView = view.findViewById(R.id.wallet_wizard_smarcard_back);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_splash, viewGroup, false);
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      long startFlipDelay = FLIP_ANIM_DELAY;
      if (!fadeInAnimationFinished) {
         startFadeInAnimation();
         startFlipDelay = 0;
      }

      flipAnim = FlipAnim.builder()
            .setCardBackLayout(backView)
            .setCardFrontLayout(frontView)
            .flipCard(startFlipDelay);
   }

   @Override
   protected void onDetach(@NonNull View view) {
      super.onDetach(view);
      flipAnim.cancel();
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   private void startFadeInAnimation() {
      actionBtn.setAlpha(0f);
      walletWizardSplashTitle.setAlpha(0f);
      cardContainer.setAlpha(0f);

      AnimatorSet mainAnimation = new AnimatorSet();
      mainAnimation
            .play(ofFloat(actionBtn, View.ALPHA, 1).setDuration(COMMON_FADE_IN_DURATION))
            .with(ofFloat(walletWizardSplashTitle, View.ALPHA, 1).setDuration(COMMON_FADE_IN_DURATION))
            .after(ofFloat(cardContainer, View.ALPHA, 1).setDuration(CARD_FADE_IN_DURATION));

      mainAnimation.start();
      fadeInAnimationFinished = true;
   }

   @Override
   public WizardSplashPresenter getPresenter() {
      return presenter;
   }

   @Nullable
   @Override
   protected Object screenModule() {
      return new WizardSplashScreenModule();
   }

   @Override
   protected void onSaveViewState(@NonNull View view, @NonNull Bundle outState) {
      super.onSaveViewState(view, outState);
      outState.putBoolean(KEY_STATE_FADE_IN_ANIMATION_STATE, fadeInAnimationFinished);
   }

   @Override
   protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
      super.onRestoreViewState(view, savedViewState);
      fadeInAnimationFinished = savedViewState.getBoolean(KEY_STATE_FADE_IN_ANIMATION_STATE, false);
   }
}
