package com.worldventures.dreamtrips.wallet.ui.wizard.splash;

import android.animation.AnimatorSet;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;

import static android.animation.ObjectAnimator.ofFloat;
import static butterknife.ButterKnife.apply;
import static java.util.Arrays.asList;

public class WizardSplashScreen extends WalletFrameLayout<WizardSplashPresenter.Screen, WizardSplashPresenter, WizardSplashPath> implements WizardSplashPresenter.Screen {

   private static final int SHOW_SOAR_TITLE_DELAY = 1000;
   private static final int SOAR_FADE_OUT_DELAY = 400;
   private static final int CARD_FADE_IN_DELAY = 300;
   private static final int COMMON_FADE_IN_DELAY = 250;

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.wallet_wizard_splash_title) TextView walletWizardSplashTitle;
   @InjectView(R.id.wallet_wizard_splash_soar_title) TextView walletWizardSplashSoarTitle;
   @InjectView(R.id.wallet_wizard_splash_btn) Button actionBtn;

   @InjectView(R.id.wallet_wizard_smarcard_front) ImageView cardImage;

   public WizardSplashScreen(Context context) {
      super(context);
   }

   public WizardSplashScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public WizardSplashPresenter createPresenter() {
      return new WizardSplashPresenter(getContext(), getInjector(), getPath().termsAccepted);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> presenter.onBack());
      hideAllView();
   }

   private void hideAllView() {
      if (isInEditMode()) return;
      apply(
            asList(walletWizardSplashTitle, walletWizardSplashSoarTitle, actionBtn, cardImage),
            (view, index) -> view.setAlpha(0)
      );
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public void setup(boolean termsAccepted) {
      if (termsAccepted) {
         apply(asList(walletWizardSplashTitle, actionBtn, cardImage), (view, index) -> view.setAlpha(1));

         actionBtn.setText(R.string.wallet_wizard_scan_start_btn);
         actionBtn.setOnClickListener(view -> getPresenter().startScanCard());
         walletWizardSplashTitle.setText(R.string.wallet_wizard_scan_proposal);
         cardImage.setImageResource(R.drawable.flyecard_back);
      } else {
         walletWizardSplashSoarTitle.setAlpha(1);

         actionBtn.setText(R.string.wallet_wizard_splash_review_btn);
         actionBtn.setOnClickListener(view -> getPresenter().openTerms());
         walletWizardSplashTitle.setText(R.string.wallet_wizard_splash_title2);
      }

      setDefaultAlpha();

      postDelayed(this::startSoarAnimation,
            termsAccepted ? 0 : SHOW_SOAR_TITLE_DELAY
      );
   }

   private void setDefaultAlpha() {
      actionBtn.setAlpha(0f);
      walletWizardSplashTitle.setAlpha(0f);
      cardImage.setAlpha(0f);
   }

   private void startSoarAnimation() {
      AnimatorSet animation = new AnimatorSet();

      AnimatorSet mainAnimation = new AnimatorSet();
      mainAnimation
            .play(ofFloat(actionBtn, View.ALPHA, 1).setDuration(COMMON_FADE_IN_DELAY))
            .with(ofFloat(walletWizardSplashTitle, View.ALPHA, 1).setDuration(COMMON_FADE_IN_DELAY))
            .after(ofFloat(cardImage, View.ALPHA, 1).setDuration(CARD_FADE_IN_DELAY));

      animation
            .play(ofFloat(walletWizardSplashSoarTitle, View.ALPHA, 0).setDuration(SOAR_FADE_OUT_DELAY))
            .before(mainAnimation);

      animation.start();
   }
}
