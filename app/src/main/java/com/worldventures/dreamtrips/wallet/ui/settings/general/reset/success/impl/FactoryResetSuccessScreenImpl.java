package com.worldventures.dreamtrips.wallet.ui.settings.general.reset.success.impl;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.success.FactoryResetSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.success.FactoryResetSuccessScreen;

import javax.inject.Inject;

import butterknife.OnClick;

public class FactoryResetSuccessScreenImpl extends WalletBaseController<FactoryResetSuccessScreen, FactoryResetSuccessPresenter> implements FactoryResetSuccessScreen {

   @Inject FactoryResetSuccessPresenter presenter;

   @OnClick(R.id.btn_done)
   public void onClickDone() {
      getPresenter().navigateNext();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(getView());
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
