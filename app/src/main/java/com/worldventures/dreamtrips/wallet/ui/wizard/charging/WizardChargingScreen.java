package com.worldventures.dreamtrips.wallet.ui.wizard.charging;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.charging.anim.ChargingSwipingAnimations;

import butterknife.InjectView;

import static com.worldventures.dreamtrips.wallet.ui.wizard.charging.anim.ChargingSwipingAnimations.BANKCARD_ANIMATION_REPEAT_DEFAULT;

public class WizardChargingScreen extends WalletLinearLayout<WizardChargingPresenter.Screen, WizardChargingPresenter, WizardChargingPath> implements WizardChargingPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.smart_card) View smartCard;
   @InjectView(R.id.credit_card) View creditCard;

   private final OperationScreen operationScreen = new DialogOperationScreen(this);
   private final ChargingSwipingAnimations swipingAnimations = new ChargingSwipingAnimations();

   public WizardChargingScreen(Context context) {
      super(context);
   }

   public WizardChargingScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> navigateClick());
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      swipingAnimations.animateSmartCard(smartCard);
      swipingAnimations.animateBankCard(creditCard, BANKCARD_ANIMATION_REPEAT_DEFAULT);
   }

   @NonNull
   @Override
   public WizardChargingPresenter createPresenter() {
      return new WizardChargingPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return operationScreen;
   }

   private void navigateClick() {
      presenter.goBack();
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}
