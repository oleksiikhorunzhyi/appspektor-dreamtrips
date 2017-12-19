package com.worldventures.wallet.ui.settings.general.firmware.preinstalletion.impl;


import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.settings.general.firmware.preinstalletion.WalletFirmwareChecksPresenter;
import com.worldventures.wallet.ui.settings.general.firmware.preinstalletion.WalletFirmwareChecksScreen;
import com.worldventures.wallet.ui.widget.WalletCheckWidget;

import javax.inject.Inject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class WalletFirmwareChecksScreenImpl extends WalletBaseController<WalletFirmwareChecksScreen, WalletFirmwareChecksPresenter> implements WalletFirmwareChecksScreen {

   private WalletCheckWidget checkWidgetBattery;
   private WalletCheckWidget checkWidgetConnection;
   private WalletCheckWidget checkWidgetBluetooth;
   private WalletCheckWidget checkWidgetCharger;
   private Button installButton;
   private TextView tvInstallLater;

   @Inject WalletFirmwareChecksPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      checkWidgetBattery = view.findViewById(R.id.check_widget_battery);
      checkWidgetConnection = view.findViewById(R.id.check_widget_connection);
      checkWidgetBluetooth = view.findViewById(R.id.check_widget_bluetooth);
      checkWidgetCharger = view.findViewById(R.id.check_widget_charger);
      installButton = view.findViewById(R.id.install);
      installButton.setOnClickListener(installBtn -> getPresenter().install());
      tvInstallLater = view.findViewById(R.id.install_later);
      tvInstallLater.setOnClickListener(installLaterBtn -> getPresenter().installLater());
   }

   @Override
   public void bluetoothEnabled(boolean enabled) {
      checkWidgetBluetooth.setChecked(enabled);
   }

   @Override
   public void cardConnected(boolean connected) {
      checkWidgetConnection.setChecked(connected);
   }

   @Override
   public void cardCharged(boolean charged) {
      checkWidgetBattery.setChecked(charged);
   }

   @Override
   public void connectionStatusVisible(boolean isVisible) {
      checkWidgetConnection.setVisibility(isVisible ? VISIBLE : GONE);
   }

   @Override
   public void chargedStatusVisible(boolean isVisible) {
      checkWidgetBattery.setVisibility(isVisible ? VISIBLE : GONE);
   }

   @Override
   public void installButtonEnabled(boolean enabled) {
      installButton.setEnabled(enabled);
   }

   @Override
   public void cardIsInCharger(boolean enabled) {
      checkWidgetCharger.setChecked(enabled);
   }

   @Override
   public void cardIsInChargerCheckVisible(boolean isVisible) {
      checkWidgetCharger.setVisibility(isVisible ? VISIBLE : GONE);
   }

   @Override
   public WalletFirmwareChecksPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_preinstallation, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @Nullable
   @Override
   protected Object screenModule() {
      return new WalletFirmwareChecksScreenModule();
   }
}
