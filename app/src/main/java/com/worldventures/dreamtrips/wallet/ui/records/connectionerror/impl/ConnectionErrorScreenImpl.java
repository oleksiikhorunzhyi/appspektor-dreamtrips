package com.worldventures.dreamtrips.wallet.ui.records.connectionerror.impl;


import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.records.connectionerror.ConnectionErrorPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.connectionerror.ConnectionErrorScreen;

import javax.inject.Inject;

public class ConnectionErrorScreenImpl extends WalletBaseController<ConnectionErrorScreen, ConnectionErrorPresenter> implements ConnectionErrorScreen {

   @Inject ConnectionErrorPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_connection_error, viewGroup, false);
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
   public ConnectionErrorPresenter getPresenter() {
      return presenter;
   }
}
