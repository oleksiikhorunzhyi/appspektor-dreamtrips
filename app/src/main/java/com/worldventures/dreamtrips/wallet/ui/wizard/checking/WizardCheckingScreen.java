package com.worldventures.dreamtrips.wallet.ui.wizard.checking;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletCheckWidget;

import butterknife.InjectView;
import butterknife.OnClick;

public class WizardCheckingScreen extends WalletLinearLayout<WizardCheckingPresenter.Screen, WizardCheckingPresenter, WizardCheckingPath>
      implements WizardCheckingPresenter.Screen {

   @InjectView(R.id.check_widget_wifi) WalletCheckWidget checkInternet;
   @InjectView(R.id.check_widget_bluetooth) WalletCheckWidget checkBluetooth;
   @InjectView(R.id.next_button) View nextButton;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   public WizardCheckingScreen(Context context) {
      super(context);
   }

   public WizardCheckingScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @NonNull
   @Override
   public WizardCheckingPresenter createPresenter() {
      return new WizardCheckingPresenter(getContext(), getInjector());
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
      presenter.goNext();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }
}
