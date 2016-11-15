package com.worldventures.dreamtrips.wallet.ui.settings.firmware.puck_connection;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.charging.anim.ChargingSwipingAnimations;

import butterknife.InjectView;
import butterknife.OnClick;

public class WalletPuckConnectionScreen extends WalletLinearLayout<WalletPuckConnectionPresenter.Screen, WalletPuckConnectionPresenter, WalletPuckConnectionPath>
      implements WalletPuckConnectionPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.smart_card) View smartCard;

   private final ChargingSwipingAnimations swipingAnimations = new ChargingSwipingAnimations();

   public WalletPuckConnectionScreen(Context context) {
      super(context);
   }

   public WalletPuckConnectionScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public WalletPuckConnectionPresenter createPresenter() {
      return new WalletPuckConnectionPresenter(getContext(), getInjector(), getPath().firmwareInfo, getPath().filePath);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();

      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
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
}