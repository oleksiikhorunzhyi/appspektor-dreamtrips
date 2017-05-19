package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.anim.ChargingSwipingAnimations;

import butterknife.InjectView;
import butterknife.OnClick;

public class WalletPuckConnectionScreen extends WalletLinearLayout<WalletPuckConnectionPresenter.Screen, WalletPuckConnectionPresenter, WalletPuckConnectionPath>
      implements WalletPuckConnectionPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.smart_card) View smartCard;
   @InjectView(R.id.user_photo) SimpleDraweeView userPhoto;

   private final ChargingSwipingAnimations swipingAnimations = new ChargingSwipingAnimations();

   public WalletPuckConnectionScreen(Context context) {
      super(context);
   }

   public WalletPuckConnectionScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public WalletPuckConnectionPresenter createPresenter() {
      return new WalletPuckConnectionPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      supportConnectionStatusLabel(false);
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
      userPhoto.getHierarchy().setActualImageFocusPoint(new PointF(0f, .5f));
      ImageUtils.applyGrayScaleColorFilter(userPhoto);
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      swipingAnimations.animateSmartCard(smartCard);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @OnClick(R.id.next_button)
   void nextButtonClick() {
      getPresenter().goNext();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public void userPhoto(String photoUrl) {
      userPhoto.setImageURI(photoUrl);
   }
}
