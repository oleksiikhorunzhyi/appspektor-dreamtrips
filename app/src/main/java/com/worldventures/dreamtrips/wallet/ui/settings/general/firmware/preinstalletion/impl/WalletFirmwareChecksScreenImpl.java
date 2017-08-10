package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion.impl;


import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion.WalletFirmwareChecksPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion.WalletFirmwareChecksScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletCheckWidget;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class WalletFirmwareChecksScreenImpl extends WalletBaseController<WalletFirmwareChecksScreen, WalletFirmwareChecksPresenter> implements WalletFirmwareChecksScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.check_widget_battery) WalletCheckWidget checkWidgetButtery;
   @InjectView(R.id.check_widget_connection) WalletCheckWidget checkWidgetConnection;
   @InjectView(R.id.check_widget_bluetooth) WalletCheckWidget checkWidgetBluetooth;
   @InjectView(R.id.check_widget_charger) WalletCheckWidget checkWidgetCharger;
   @InjectView(R.id.install) Button installButton;
   @InjectView(R.id.install_later) TextView tvInstallLater;

   @Inject WalletFirmwareChecksPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @OnClick(R.id.install_later)
   protected void installLaterClick() {
      getPresenter().installLater();
   }

   @OnClick(R.id.install)
   protected void installClick() {
      getPresenter().install();
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
      checkWidgetButtery.setChecked(charged);
   }

   @Override
   public void connectionStatusVisible(boolean isVisible) {
      checkWidgetConnection.setVisibility(isVisible ? VISIBLE : GONE);
   }

   @Override
   public void chargedStatusVisible(boolean isVisible) {
      checkWidgetButtery.setVisibility(isVisible ? VISIBLE : GONE);
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
}
