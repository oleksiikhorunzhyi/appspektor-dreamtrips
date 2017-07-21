package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.impl;


import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.WalletProvisioningBlockedPresenter;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.WalletProvisioningBlockedScreen;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.SupportedDevicesListCell;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.SupportedDevicesListModel;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.UnsupportedDeviceInfoCell;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.UnsupportedDeviceModel;

import javax.inject.Inject;

import butterknife.InjectView;

public class WalletProvisioningBlockedScreenImpl extends WalletBaseController<WalletProvisioningBlockedScreen, WalletProvisioningBlockedPresenter> implements WalletProvisioningBlockedScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.recycler_view) RecyclerView deviceList;

   @Inject WalletProvisioningBlockedPresenter presenter;

   IgnoreFirstItemAdapter adapter;

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);

      toolbar.setNavigationOnClickListener(v -> onNavigationClick());

      adapter = new IgnoreFirstItemAdapter(getContext(), (Injector) getContext());

      adapter.registerCell(UnsupportedDeviceModel.class, UnsupportedDeviceInfoCell.class);
      adapter.registerCell(SupportedDevicesListModel.class, SupportedDevicesListCell.class);
      adapter.addItem(0, new UnsupportedDeviceModel());

      deviceList.setAdapter(adapter);
      LinearLayoutManager layout = new LinearLayoutManager(getContext());
      layout.setAutoMeasureEnabled(true);
      deviceList.setLayoutManager(layout);
   }

   private void onNavigationClick() {
      getPresenter().goBack();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(getView());
   }

   @Override
   public void onSupportedDevicesLoaded(SupportedDevicesListModel devicesModel) {
      adapter.clear();
      adapter.addItem(devicesModel);
   }

   @Override
   public WalletProvisioningBlockedPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_provisioning_blocked, viewGroup, false);
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
