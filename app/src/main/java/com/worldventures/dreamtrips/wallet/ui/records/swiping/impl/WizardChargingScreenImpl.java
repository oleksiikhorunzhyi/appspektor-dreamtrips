package com.worldventures.dreamtrips.wallet.ui.records.swiping.impl;


import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.WizardChargingPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.WizardChargingScreen;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.anim.ChargingSwipingAnimations;

import javax.inject.Inject;

import butterknife.InjectView;

public class WizardChargingScreenImpl extends WalletBaseController<WizardChargingScreen, WizardChargingPresenter> implements WizardChargingScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.smart_card) View smartCard;
   @InjectView(R.id.credit_card) View creditCard;
   @InjectView(R.id.user_photo) SimpleDraweeView userPhoto;

   @Inject WizardChargingPresenter presenter;

   private final ChargingSwipingAnimations swipingAnimations = new ChargingSwipingAnimations();
   private OperationScreen operationScreen;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> navigateClick());
      userPhoto.getHierarchy().setActualImageFocusPoint(new PointF(0f, .5f));
      ImageUtils.applyGrayScaleColorFilter(userPhoto);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_charging, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      operationScreen = new DialogOperationScreen(getView());
      swipingAnimations.animateSmartCard(smartCard);
      swipingAnimations.animateBankCard(creditCard, Animation.INFINITE);
   }

   @Override
   public WizardChargingPresenter getPresenter() {
      return presenter;
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return operationScreen;
   }

   private void navigateClick() {
      getPresenter().goBack();
   }

   @Override
   public void showSwipeError() {
      operationScreen.showError(getString(R.string.wallet_wizard_charging_swipe_error), o -> {
      });
   }

   @Override
   public void trySwipeAgain() {
      operationScreen.showError(getString(R.string.wallet_receive_data_error), o -> {
      });
   }

   @Override
   public void showSwipeSuccess() {
      operationScreen.showProgress(getString(R.string.wallet_add_card_swipe_success));
   }

   @Override
   public void userPhoto(@Nullable SmartCardUserPhoto photo) {
      if (photo != null) {
         userPhoto.setImageURI(photo.uri());
      } // TODO: 5/23/17 add placeholder
   }
}
