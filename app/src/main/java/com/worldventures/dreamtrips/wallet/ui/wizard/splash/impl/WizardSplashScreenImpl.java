package com.worldventures.dreamtrips.wallet.ui.wizard.splash.impl;

import android.animation.AnimatorSet;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.widget.anim.FlipAnim;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashScreen;

import javax.inject.Inject;

import static android.animation.ObjectAnimator.ofFloat;
import static java.util.Arrays.asList;

public class WizardSplashScreenImpl extends WalletBaseController<WizardSplashScreen, WizardSplashPresenter> implements WizardSplashScreen {

   private static final int FLIP_ANIM_DELAY = 500;
   private static final int CARD_FADE_IN_DELAY = 300;
   private static final int COMMON_FADE_IN_DELAY = 250;

   @Inject WizardSplashPresenter presenter;

   private TextView walletWizardSplashTitle;
   private Button actionBtn;
   private View cardContainer;
   private ImageView front;
   private FlipAnim flipAnim;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().onBack());
      walletWizardSplashTitle = view.findViewById(R.id.wallet_wizard_splash_title);
      actionBtn = view.findViewById(R.id.wallet_wizard_splash_btn);
      cardContainer = view.findViewById(R.id.card_container);
      front = view.findViewById(R.id.wallet_wizard_smarcard_front);
      final View back = view.findViewById(R.id.wallet_wizard_smarcard_back);
      flipAnim = new FlipAnim.Builder().setCardBackLayout(back).setCardFrontLayout(front).createAnim();
      hideAllView();
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_splash, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   private void hideAllView() {
      for (View view : asList(walletWizardSplashTitle, actionBtn, cardContainer)) {
         view.setAlpha(0);
      }
   }

   @Override
   public void setup() {
      for (View view : asList(actionBtn, cardContainer)) {
         view.setAlpha(1);
      }

      actionBtn.setText(R.string.wallet_wizard_scan_start_btn);
      actionBtn.setOnClickListener(view -> getPresenter().startScanCard());
      walletWizardSplashTitle.setText(R.string.wallet_wizard_scan_proposal);

      front.setImageResource(R.drawable.wallet_flye_front);
      flipAnim.flipCard(FLIP_ANIM_DELAY);

      setDefaultAlpha();

      startSoarAnimation();
   }

   private void setDefaultAlpha() {
      actionBtn.setAlpha(0f);
      walletWizardSplashTitle.setAlpha(0f);
      cardContainer.setAlpha(0f);
   }

   private void startSoarAnimation() {
      AnimatorSet mainAnimation = new AnimatorSet();
      mainAnimation
            .play(ofFloat(actionBtn, View.ALPHA, 1).setDuration(COMMON_FADE_IN_DELAY))
            .with(ofFloat(walletWizardSplashTitle, View.ALPHA, 1).setDuration(COMMON_FADE_IN_DELAY))
            .after(ofFloat(cardContainer, View.ALPHA, 1).setDuration(CARD_FADE_IN_DELAY));

      mainAnimation.start();
   }

   @Override
   public WizardSplashPresenter getPresenter() {
      return presenter;
   }
}
