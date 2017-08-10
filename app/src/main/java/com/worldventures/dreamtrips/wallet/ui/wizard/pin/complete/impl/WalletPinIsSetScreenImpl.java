package com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.impl;


import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.WalletPinIsSetPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.WalletPinIsSetScreen;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

public class WalletPinIsSetScreenImpl extends WalletBaseController<WalletPinIsSetScreen, WalletPinIsSetPresenter> implements WalletPinIsSetScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @Inject WalletPinIsSetPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_pin_is_set, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @OnClick(R.id.next_button)
   public void nextClick() {
      getPresenter().navigateToNextScreen();
   }

   @Override
   public WalletPinIsSetPresenter getPresenter() {
      return presenter;
   }
}
