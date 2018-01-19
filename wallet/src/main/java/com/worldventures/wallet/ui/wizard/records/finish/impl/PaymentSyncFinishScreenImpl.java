package com.worldventures.wallet.ui.wizard.records.finish.impl;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.wizard.records.finish.PaymentSyncFinishPresenter;
import com.worldventures.wallet.ui.wizard.records.finish.PaymentSyncFinishScreen;

import javax.inject.Inject;

public class PaymentSyncFinishScreenImpl extends WalletBaseController<PaymentSyncFinishScreen, PaymentSyncFinishPresenter> implements PaymentSyncFinishScreen {

   @Inject PaymentSyncFinishPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
      final Button btnDone = view.findViewById(R.id.btn_done);
      btnDone.setOnClickListener(done -> getPresenter().onDone());
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
   public PaymentSyncFinishPresenter getPresenter() {
      return presenter;
   }

   @Nullable
   @Override
   protected Object screenModule() {
      return new PaymentSyncFinishScreenModule();
   }
}
