package com.worldventures.dreamtrips.wallet.ui.records.swiping;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.anim.ChargingSwipingAnimations;

import java.io.File;

import butterknife.InjectView;

public class WizardChargingScreen extends WalletLinearLayout<WizardChargingPresenter.Screen, WizardChargingPresenter, WizardChargingPath> implements WizardChargingPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.smart_card) View smartCard;
   @InjectView(R.id.credit_card) View creditCard;
   @InjectView(R.id.user_photo) SimpleDraweeView userPhoto;

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
      toolbar.setNavigationOnClickListener(v -> navigateClick());
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      swipingAnimations.animateSmartCard(smartCard);
      swipingAnimations.animateBankCard(creditCard, Animation.INFINITE);
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

   @Override
   public void checkConnection(ConnectionStatus connectionStatus) {
      if(!connectionStatus.isConnected()) presenter.showConnectionErrorScreen();
   }

   @Override
   public void showSwipeError() {
      operationScreen.showError(getString(R.string.wallet_wizard_charging_swipe_error), o -> {});
   }

   @Override
   public void trySwipeAgain() {
      operationScreen.showError(getString(R.string.wallet_receive_data_error), o -> {});
   }

   @Override
   public void showSwipeSuccess() {
      operationScreen.showProgress(getString(R.string.wallet_add_card_swipe_success));
   }

   @Override
   public void userPhoto(File photo) {
      userPhoto.setImageURI(Uri.fromFile(photo));
   }
}
