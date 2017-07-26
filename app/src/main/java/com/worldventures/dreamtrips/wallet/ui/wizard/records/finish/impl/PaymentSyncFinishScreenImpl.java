package com.worldventures.dreamtrips.wallet.ui.wizard.records.finish.impl;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.finish.PaymentSyncFinishPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.finish.PaymentSyncFinishScreen;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

public class PaymentSyncFinishScreenImpl extends WalletBaseController<PaymentSyncFinishScreen, PaymentSyncFinishPresenter> implements PaymentSyncFinishScreen{

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @Inject PaymentSyncFinishPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_payment_sync_complete, viewGroup, false);
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
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @OnClick(R.id.btn_done)
   public void onClickDone() {
      getPresenter().onDone();
   }

   @Override
   public PaymentSyncFinishPresenter getPresenter() {
      return presenter;
   }
}
