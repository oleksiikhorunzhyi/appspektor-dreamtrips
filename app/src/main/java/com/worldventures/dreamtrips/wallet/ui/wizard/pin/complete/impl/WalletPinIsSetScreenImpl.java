package com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.impl;


import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.WalletPinIsSetPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.WalletPinIsSetScreen;

import javax.inject.Inject;

public class WalletPinIsSetScreenImpl extends WalletBaseController<WalletPinIsSetScreen, WalletPinIsSetPresenter> implements WalletPinIsSetScreen {

   @Inject WalletPinIsSetPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      final Button btnNext = view.findViewById(R.id.next_button);
      btnNext.setOnClickListener(next -> getPresenter().navigateToNextScreen());
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

   @Override
   public WalletPinIsSetPresenter getPresenter() {
      return presenter;
   }
}
