package com.worldventures.dreamtrips.wallet.ui.wizard.power_on;

import android.animation.AnimatorSet;
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

import static android.animation.ObjectAnimator.ofFloat;
import static butterknife.ButterKnife.apply;
import static java.util.Arrays.asList;

public class WizardPowerOnScreen extends WalletLinearLayout<WizardPowerOnPresenter.Screen, WizardPowerOnPresenter, WizardPowerOnPath> implements WizardPowerOnPresenter.Screen {

   private static final int SHOW_SOAR_TITLE_DELAY = 1000;
   private static final int SOAR_FADE_OUT_DELAY = 400;
   private static final int CARD_FADE_IN_DELAY = 300;
   private static final int COMMON_FADE_IN_DELAY = 250;

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.wallet_wizard_power_on_title) TextView walletWizardSplashTitle;
   @InjectView(R.id.wallet_wizard_power_on_soar_title) TextView walletWizardSplashSoarTitle;
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
      toolbar.setNavigationOnClickListener(v -> presenter.onBack());

      actionBtn.setOnClickListener(view -> getPresenter().openWelcome());
      hideAllView();
      postDelayed(this::startSoarAnimation, SHOW_SOAR_TITLE_DELAY);
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   private void hideAllView() {
      if (isInEditMode()) return;
      apply(
            asList(actionBtn, walletWizardSplashTitle, cardContainer),
            (view, index) -> view.setAlpha(0)
      );
   }

   private void startSoarAnimation() {
      AnimatorSet animation = new AnimatorSet();

      AnimatorSet mainAnimation = new AnimatorSet();
      mainAnimation
            .play(ofFloat(actionBtn, View.ALPHA, 1).setDuration(COMMON_FADE_IN_DELAY))
            .with(ofFloat(walletWizardSplashTitle, View.ALPHA, 1).setDuration(COMMON_FADE_IN_DELAY))
            .after(ofFloat(cardContainer, View.ALPHA, 1).setDuration(CARD_FADE_IN_DELAY));

      animation
            .play(ofFloat(walletWizardSplashSoarTitle, View.ALPHA, 0).setDuration(SOAR_FADE_OUT_DELAY))
            .before(mainAnimation);

      animation.start();
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}
