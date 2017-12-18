package com.worldventures.wallet.ui.settings.general.reset.success.impl;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.settings.general.reset.success.FactoryResetSuccessPresenter;
import com.worldventures.wallet.ui.settings.general.reset.success.FactoryResetSuccessScreen;

import javax.inject.Inject;

public class FactoryResetSuccessScreenImpl extends WalletBaseController<FactoryResetSuccessScreen, FactoryResetSuccessPresenter> implements FactoryResetSuccessScreen {

   @Inject FactoryResetSuccessPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Button btnDone = view.findViewById(R.id.btn_done);
      btnDone.setOnClickListener(doneBtn -> getPresenter().navigateNext());
   }

   @Override
   public FactoryResetSuccessPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_factory_reset_success, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }
}
