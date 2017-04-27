package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.SupportedDevicesListCell;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.SupportedDevicesListModel;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.UnsupportedDeviceInfoCell;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.UnsupportedDeviceModel;

import butterknife.InjectView;

public class WalletProvisioningBlockedScreen extends WalletLinearLayout<WalletProvisioningBlockedPresenter.Screen, WalletProvisioningBlockedPresenter, WalletProvisioningBlockedPath> implements WalletProvisioningBlockedPresenter.Screen {

   public WalletProvisioningBlockedScreen(Context context) {
      super(context);
   }

   public WalletProvisioningBlockedScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.recycler_view) RecyclerView deviceList;
   IgnoreFirstItemAdapter adapter;

   @Override
   public WalletProvisioningBlockedPresenter createPresenter() {
      return new WalletProvisioningBlockedPresenter(getContext(), getInjector());
   }

   @Override
   protected void onPostAttachToWindowView() {
      super.onPostAttachToWindowView();

      toolbar.setNavigationOnClickListener(v -> onNavigationClick());

      adapter = new IgnoreFirstItemAdapter(getContext(), getInjector());

      adapter.registerCell(UnsupportedDeviceModel.class, UnsupportedDeviceInfoCell.class);
      adapter.registerCell(SupportedDevicesListModel.class, SupportedDevicesListCell.class);
      adapter.addItem(0, new UnsupportedDeviceModel());

      deviceList.setAdapter(adapter);
      LinearLayoutManager layout = new LinearLayoutManager(getContext());
      layout.setAutoMeasureEnabled(true);
      deviceList.setLayoutManager(layout);
   }
   
   private void onNavigationClick() {
      presenter.goBack();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   public void onSupportedDevicesLoaded(SupportedDevicesListModel devicesModel) {
      adapter.clear();
      adapter.addItem(devicesModel);
   }
}
