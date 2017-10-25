package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.impl;


import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.anim.ChargingSwipingAnimations;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.WalletPuckConnectionPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.WalletPuckConnectionScreen;
import com.worldventures.dreamtrips.wallet.util.SmartCardAvatarHelper;

import javax.inject.Inject;

public class WalletPuckConnectionScreenImpl extends WalletBaseController<WalletPuckConnectionScreen, WalletPuckConnectionPresenter> implements WalletPuckConnectionScreen {

   @Inject WalletPuckConnectionPresenter presenter;

   private final ChargingSwipingAnimations swipingAnimations = new ChargingSwipingAnimations();

   private View smartCard;
   private SimpleDraweeView userPhoto;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      smartCard = view.findViewById(R.id.smart_card);
      userPhoto = view.findViewById(R.id.user_photo);
      userPhoto.getHierarchy().setActualImageFocusPoint(new PointF(0f, .5f));
      SmartCardAvatarHelper.applyGrayScaleColorFilter(userPhoto);
      final Button btnNext = view.findViewById(R.id.next_button);
      btnNext.setOnClickListener(btn -> getPresenter().goNext());
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

   @Override
   public void userPhoto(@Nullable SmartCardUserPhoto photo) {
      if (photo != null) {
         userPhoto.setImageURI(photo.uri());
      } // // TODO: 5/23/17 add place holder
   }
}
