package com.worldventures.dreamtrips.wallet.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;
import com.worldventures.dreamtrips.modules.picker.service.MediaPickerFacebookService;
import com.worldventures.dreamtrips.wallet.di.WalletAppModule;
import com.worldventures.dreamtrips.wallet.service.WalletCropImageService;
import com.worldventures.dreamtrips.wallet.ui.common.LocationScreenComponent;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletActivityPresenter;
import com.worldventures.dreamtrips.wallet.ui.start.WalletStartPath;

import javax.inject.Inject;

import flow.History;

@Layout(R.layout.activity_wallet)
public class WalletActivity extends FlowActivity<WalletActivityPresenter> implements WalletActivityPresenter.View {

   private static final int REQUEST_CODE_BLUETOOTH_ON = 0xF045;

   private final LocationScreenComponent locationSettingsService = new LocationScreenComponent(this);

   @Inject PhotoPickerLayoutDelegate photoPickerLayoutDelegate;
   @Inject WalletCropImageService cropImageDelegate;
   @Inject MediaPickerFacebookService walletPickerFacebookService;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      navigationDrawerPresenter.setCurrentComponent(rootComponentsProvider.getComponentByKey(WalletAppModule.WALLET));
   }

   @Override
   public void onDestroy() {
      cropImageDelegate.destroy();
      super.onDestroy();
   }

   @Override
   protected ComponentDescription getCurrentComponent() {
      return rootComponentsProvider.getComponentByKey(WalletAppModule.WALLET);
   }

   @Override
   protected History provideDefaultHistory() {
      return History.single(new WalletStartPath());
   }

   @Override
   protected WalletActivityPresenter createPresentationModel(Bundle savedInstanceState) {
      return new WalletActivityPresenter();
   }

   public static void startWallet(Context context) {
      context.startActivity(new Intent(context, WalletActivity.class));
   }

   @Override
   public Object getSystemService(@NonNull String name) {
      if (WalletCropImageService.SERVICE_NAME.equals(name)) {
         return cropImageDelegate;
      } else if (LocationScreenComponent.COMPONENT_NAME.equals(name)) {
         return locationSettingsService;
      }
      return super.getSystemService(name);
   }

   public void openBluetoothSettings() {
      Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      this.startActivityForResult(requestBluetoothOn, REQUEST_CODE_BLUETOOTH_ON);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (cropImageDelegate.onActivityResult(requestCode, resultCode, data)) return;
      if (locationSettingsService.onActivityResult(requestCode, resultCode, data)) return;
      if (walletPickerFacebookService.onActivityResult(requestCode, resultCode, data)) return;
      super.onActivityResult(requestCode, resultCode, data);
   }
}
