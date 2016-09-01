package com.worldventures.dreamtrips.wallet.ui.wizard.splash;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.anim.FlipAnim;

import butterknife.InjectView;

public class WizardSplashScreen extends WalletFrameLayout<WizardSplashPresenter.Screen, WizardSplashPresenter, WizardSplashPath> implements WizardSplashPresenter.Screen {

   private static final int FLIP_ANIM_DELAY = 500;

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.wallet_wizard_splash_title) TextView walletWizardSplashTitle;
   @InjectView(R.id.wallet_wizard_splash_btn) Button actionBtn;
   @InjectView(R.id.wallet_wizard_smarcard_front) View front;
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
      return new WizardSplashPresenter(getContext(), getInjector(), getPath().termsAccepted);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> presenter.onBack());
      flipAnim = new FlipAnim.Builder().setCardBackLayout(back).setCardFrontLayout(front).createAnim();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public void setup(boolean termsAccepted) {
      if (termsAccepted) {
         actionBtn.setText(R.string.wallet_wizard_scan_start_btn);
         actionBtn.setOnClickListener(view -> getPresenter().startScanCard());
         walletWizardSplashTitle.setText(R.string.wallet_wizard_scan_proposal);

         flipAnim.flipCard(FLIP_ANIM_DELAY);
      } else {
         actionBtn.setText(R.string.wallet_wizard_splash_review_btn);
         actionBtn.setOnClickListener(view -> getPresenter().openTerms());
         walletWizardSplashTitle.setText(R.string.wallet_wizard_splash_title2);
      }
   }
}
