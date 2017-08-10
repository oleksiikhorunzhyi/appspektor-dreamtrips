package com.worldventures.dreamtrips.wallet.ui.wizard.checking.impl;


import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletCheckWidget;
import com.worldventures.dreamtrips.wallet.ui.wizard.checking.WizardCheckingPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.checking.WizardCheckingScreen;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

public class WizardCheckingScreenImpl extends WalletBaseController<WizardCheckingScreen, WizardCheckingPresenter> implements WizardCheckingScreen {

   @InjectView(R.id.check_widget_wifi) WalletCheckWidget checkInternet;
   @InjectView(R.id.check_widget_bluetooth) WalletCheckWidget checkBluetooth;
   @InjectView(R.id.next_button) View nextButton;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   @Inject WizardCheckingPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
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
      checkInternet.setTitle(available ?
            R.string.wallet_wizard_checks_network_available :
            R.string.wallet_wizard_checks_network_not_available
      );
      checkInternet.setChecked(available);
   }

   @Override
   public void bluetoothEnable(boolean enable) {
      checkBluetooth.setTitle(enable ?
            R.string.wallet_wizard_checks_bluetooth_enable :
            R.string.wallet_wizard_checks_bluetooth_not_enable);
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

   @OnClick(R.id.next_button)
   protected void onNextClick() {
      getPresenter().goNext();
   }

   @Override
   public WizardCheckingPresenter getPresenter() {
      return presenter;
   }
}
