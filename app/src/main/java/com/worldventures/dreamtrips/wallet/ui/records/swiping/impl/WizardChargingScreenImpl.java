package com.worldventures.dreamtrips.wallet.ui.records.swiping.impl;


import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.view.ImageUtils;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SCConnectionErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.WizardChargingPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.WizardChargingScreen;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.anim.ChargingSwipingAnimations;

import javax.inject.Inject;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction;

public class WizardChargingScreenImpl extends WalletBaseController<WizardChargingScreen, WizardChargingPresenter> implements WizardChargingScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.smart_card) View smartCard;
   @InjectView(R.id.credit_card) View creditCard;
   @InjectView(R.id.user_photo) SimpleDraweeView userPhoto;

   @Inject WizardChargingPresenter presenter;

   private final ChargingSwipingAnimations swipingAnimations = new ChargingSwipingAnimations();

   private MaterialDialog dialog;

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
      swipingAnimations.animateSmartCard(smartCard);
      swipingAnimations.animateBankCard(creditCard, Animation.INFINITE);
   }

   @Override
   public WizardChargingPresenter getPresenter() {
      return presenter;
   }

   private void navigateClick() {
      getPresenter().goBack();
   }

   @Override
   public void showSwipeError() {
      showDialog(getString(R.string.wallet_wizard_charging_swipe_error), false);
   }

   @Override
   public void trySwipeAgain() {
      showDialog(getString(R.string.wallet_receive_data_error), false);
   }

   @Override
   public void showSwipeSuccess() {
      showDialog(getString(R.string.wallet_add_card_swipe_success), true);
   }

   private void showDialog(String message, boolean withProgress) {
      final MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext())
            .content(message);

      if (withProgress) {
         builder.progress(true, 0);
      } else {
         builder.positiveText(R.string.ok);
         builder.onPositive((dialog, which) -> {
            dialog.dismiss();
            getPresenter().goBack();
         });
      }

      if (dialog != null) dialog.dismiss();
      dialog = builder.build();
      dialog.show();
   }

   @Override
   public void userPhoto(@Nullable SmartCardUserPhoto photo) {
      if (photo != null) {
         userPhoto.setImageURI(photo.uri());
      } // TODO: 5/23/17 add placeholder
   }

   @Override
   public OperationView<CreateRecordCommand> provideOperationCreateRecord() {
      return new ComposableOperationView<>(
            ErrorViewFactory.<CreateRecordCommand>builder()
                  .addProvider(new SCConnectionErrorViewProvider<>(getContext()))
                  .addProvider(new SmartCardErrorViewProvider<>(getContext()))
                  .build()
      );
   }

   @Override
   public OperationView<StartCardRecordingAction> provideOperationStartCardRecording() {
      return new ComposableOperationView<>(
            ErrorViewFactory.<StartCardRecordingAction>builder()
                  .addProvider(new SCConnectionErrorViewProvider<>(getContext(),
                        cmd -> trySwipeAgain(), cmd -> getPresenter().goBack()))
                  .addProvider(new SmartCardErrorViewProvider<>(getContext(),
                        cmd -> trySwipeAgain(), cmd -> getPresenter().goBack()))
                  .build()
      );
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (dialog != null) dialog.dismiss();
      super.onDetach(view);
   }
}
