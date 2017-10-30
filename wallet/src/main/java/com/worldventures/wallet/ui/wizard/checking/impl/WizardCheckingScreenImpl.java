package com.worldventures.wallet.ui.wizard.checking.impl;


import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.widget.WalletCheckWidget;
import com.worldventures.wallet.ui.wizard.checking.WizardCheckingPresenter;
import com.worldventures.wallet.ui.wizard.checking.WizardCheckingScreen;

import javax.inject.Inject;

public class WizardCheckingScreenImpl extends WalletBaseController<WizardCheckingScreen, WizardCheckingPresenter> implements WizardCheckingScreen {

   private WalletCheckWidget checkInternet;
   private WalletCheckWidget checkBluetooth;
   private Button nextButton;

   @Inject WizardCheckingPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      checkInternet = view.findViewById(R.id.check_widget_wifi);
      checkBluetooth = view.findViewById(R.id.check_widget_bluetooth);
      nextButton = view.findViewById(R.id.next_button);
      nextButton.setOnClickListener(nextBtn -> getPresenter().goNext());
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_checking, viewGroup, false);
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
   public void networkAvailable(boolean available) {
      checkInternet.setTitle(available
            ? R.string.wallet_wizard_checks_network_available
            : R.string.wallet_wizard_checks_network_not_available
      );
      checkInternet.setChecked(available);
   }

   @Override
   public void bluetoothEnable(boolean enable) {
      checkBluetooth.setTitle(enable
            ? R.string.wallet_wizard_checks_bluetooth_enable
            : R.string.wallet_wizard_checks_bluetooth_not_enable);
      checkBluetooth.setChecked(enable);
   }

   @Override
   public void bluetoothDoesNotSupported() {
      checkBluetooth.setTitle(R.string.wallet_wizard_checks_bluetooth_is_not_supported);
   }

   @Override
   public void buttonEnable(boolean enable) {
      nextButton.setEnabled(enable);
   }

   @Override
   public WizardCheckingPresenter getPresenter() {
      return presenter;
   }
}
