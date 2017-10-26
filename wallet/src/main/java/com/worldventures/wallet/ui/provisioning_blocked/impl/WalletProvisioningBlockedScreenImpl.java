package com.worldventures.wallet.ui.provisioning_blocked.impl;


import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.wallet.R;
import com.worldventures.wallet.service.command.GetCompatibleDevicesCommand;
import com.worldventures.wallet.ui.common.adapter.SimpleMultiHolderAdapter;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.wallet.ui.common.helper2.error.SCConnectionErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.wallet.ui.provisioning_blocked.WalletProvisioningBlockedPresenter;
import com.worldventures.wallet.ui.provisioning_blocked.WalletProvisioningBlockedScreen;
import com.worldventures.wallet.ui.provisioning_blocked.adapter.ProvisionBlockedHolderFactoryImpl;
import com.worldventures.wallet.ui.provisioning_blocked.holder.CustomerSupportContactModel;
import com.worldventures.wallet.ui.provisioning_blocked.holder.SupportedDevicesListModel;
import com.worldventures.wallet.ui.provisioning_blocked.holder.UnsupportedDeviceModel;

import java.util.ArrayList;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WalletProvisioningBlockedScreenImpl extends WalletBaseController<WalletProvisioningBlockedScreen, WalletProvisioningBlockedPresenter> implements WalletProvisioningBlockedScreen {

   @Inject WalletProvisioningBlockedPresenter presenter;

   private RecyclerView deviceList;
   private SimpleMultiHolderAdapter adapter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      deviceList = view.findViewById(R.id.recycler_view);
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      adapter = new SimpleMultiHolderAdapter<>(new ArrayList<>(), new ProvisionBlockedHolderFactoryImpl());

      adapter.addItem(new UnsupportedDeviceModel());
      adapter.addItem(new CustomerSupportContactModel());

      deviceList.setAdapter(adapter);
      LinearLayoutManager layout = new LinearLayoutManager(getContext());
      layout.setAutoMeasureEnabled(true);
      deviceList.setLayoutManager(layout);
   }

   private void onNavigationClick() {
      getPresenter().goBack();
   }

   @Override
   public void onSupportedDevicesLoaded(SupportedDevicesListModel devicesModel) {
      adapter.clearWithoutFirst();
      adapter.addItem(devicesModel);
      adapter.addItem(new CustomerSupportContactModel());
   }

   @Override
   public OperationView<GetCompatibleDevicesCommand> provideOperationGetCompatibleDevices() {
      return new ComposableOperationView<>(
            ErrorViewFactory.<GetCompatibleDevicesCommand>builder()
                  .addProvider(new SCConnectionErrorViewProvider<>(getContext()))
                  .addProvider(new SmartCardErrorViewProvider<>(getContext()))
                  .build()
      );
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
