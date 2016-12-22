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
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.anim.FlipAnim;

import butterknife.InjectView;

import static android.animation.ObjectAnimator.ofFloat;
import static butterknife.ButterKnife.apply;
import static java.util.Arrays.asList;

public class WizardSplashScreen extends WalletLinearLayout<WizardSplashPresenter.Screen, WizardSplashPresenter, WizardSplashPath> implements WizardSplashPresenter.Screen {

   private static final int FLIP_ANIM_DELAY = 500;

   private static final int SHOW_SOAR_TITLE_DELAY = 1000;
   private static final int SOAR_FADE_OUT_DELAY = 400;
   private static final int CARD_FADE_IN_DELAY = 300;
   private static final int COMMON_FADE_IN_DELAY = 250;

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.wallet_wizard_splash_title) TextView walletWizardSplashTitle;
   @InjectView(R.id.wallet_wizard_splash_btn) Button actionBtn;
   @InjectView(R.id.wallet_textview_info_power_on) TextView walletHelpForScanTitle;

   @InjectView(R.id.card_container) View cardContainer;
   @InjectView(R.id.wallet_wizard_smarcard_front) ImageView front;
   @InjectView(R.id.wallet_wizard_smarcard_back) View back;

   private FlipAnim flipAnim;

   public WizardSplashScreen(Context context) {
      super(context);
   }

   public WizardSplashScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public WizardSplashPresenter createPresenter() {
      return new WizardSplashPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> presenter.onBack());
      flipAnim = new FlipAnim.Builder().setCardBackLayout(back).setCardFrontLayout(front).createAnim();

      hideAllView();
   }

   private void hideAllView() {
      if (isInEditMode()) return;
      apply(
            asList(walletWizardSplashTitle, actionBtn, cardContainer, walletHelpForScanTitle),
            (view, index) -> view.setAlpha(0)
      );
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public void setup() {
      apply(asList(actionBtn, cardContainer, walletHelpForScanTitle), (view, index) -> view.setAlpha(1));

      actionBtn.setText(R.string.wallet_wizard_scan_start_btn);
      actionBtn.setOnClickListener(view -> getPresenter().startScanCard());
      walletWizardSplashTitle.setText(R.string.wallet_wizard_scan_proposal);

      front.setImageResource(R.drawable.flyecard_front);
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
   protected boolean hasToolbar() {
      return true;
   }
}
