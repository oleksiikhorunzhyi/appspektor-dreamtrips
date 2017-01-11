package com.worldventures.dreamtrips.wallet.ui.settings.firmware.force.poweron;

import android.animation.AnimatorSet;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;

import static android.animation.ObjectAnimator.ofFloat;
import static butterknife.ButterKnife.apply;
import static java.util.Arrays.asList;

public class ForceUpdatePowerOnScreen extends WalletLinearLayout<ForceUpdatePowerOnPresenter.Screen, ForceUpdatePowerOnPresenter, ForceUpdatePowerOnPath> implements ForceUpdatePowerOnPresenter.Screen {

   private static final int SHOW_SOAR_TITLE_DELAY = 1000;
   private static final int CARD_FADE_IN_DELAY = 300;
   private static final int COMMON_FADE_IN_DELAY = 250;

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.wallet_wizard_power_on_title) TextView walletWizardSplashTitle;
   @InjectView(R.id.wallet_wizard_power_on_btn) Button actionBtn;

   @InjectView(R.id.card_container) View cardContainer;

   public ForceUpdatePowerOnScreen(Context context) {
      super(context);
   }

   public ForceUpdatePowerOnScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public ForceUpdatePowerOnPresenter createPresenter() {
      return new ForceUpdatePowerOnPresenter(getPath().smartCard(), getPath().firmwareUpdateData(), getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> getPresenter().onBack());
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

   @Override
   public void setButtonAction(OnClickListener onClickListener) {
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
