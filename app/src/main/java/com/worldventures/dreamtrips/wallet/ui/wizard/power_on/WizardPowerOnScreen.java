package com.worldventures.dreamtrips.wallet.ui.wizard.power_on;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WizardVideoView;

import butterknife.InjectView;

public class WizardPowerOnScreen extends WalletLinearLayout<WizardPowerOnPresenter.Screen, WizardPowerOnPresenter, WizardPowerOnPath> implements WizardPowerOnPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.wallet_wizard_power_on_title) TextView walletWizardSplashTitle;
   @InjectView(R.id.wallet_wizard_power_on_btn) Button actionBtn;

   @InjectView(R.id.wizard_video_view) WizardVideoView wizardVideoView;

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
      supportConnectionStatusLabel(false);
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> getPresenter().onBack());
      wizardVideoView.setVideoSource(R.raw.anim_power_on_sc);
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public void setButtonAction(OnClickListener onClicklistener) {
      actionBtn.setOnClickListener(onClicklistener);
   }
}
