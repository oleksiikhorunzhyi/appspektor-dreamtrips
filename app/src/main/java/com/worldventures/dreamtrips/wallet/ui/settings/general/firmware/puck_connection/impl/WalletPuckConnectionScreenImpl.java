package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.impl;


import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.anim.ChargingSwipingAnimations;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.WalletPuckConnectionPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.WalletPuckConnectionScreen;
import com.worldventures.dreamtrips.wallet.util.SmartCardAvatarHelper;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

public class WalletPuckConnectionScreenImpl extends WalletBaseController<WalletPuckConnectionScreen, WalletPuckConnectionPresenter> implements WalletPuckConnectionScreen {
   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.smart_card) View smartCard;
   @InjectView(R.id.user_photo) SimpleDraweeView userPhoto;

   @Inject WalletPuckConnectionPresenter presenter;

   private final ChargingSwipingAnimations swipingAnimations = new ChargingSwipingAnimations();

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      userPhoto.getHierarchy().setActualImageFocusPoint(new PointF(0f, .5f));
      SmartCardAvatarHelper.applyGrayScaleColorFilter(userPhoto);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_puck_connection, viewGroup, false);
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
   }

   @Override
   public WalletPuckConnectionPresenter getPresenter() {
      return presenter;
   }


   @OnClick(R.id.next_button)
   void nextButtonClick() {
      getPresenter().goNext();
   }

   @Override
   public void userPhoto(@Nullable SmartCardUserPhoto photo) {
      if (photo != null) {
         userPhoto.setImageURI(photo.uri());
      } // // TODO: 5/23/17 add place holder
   }
}
