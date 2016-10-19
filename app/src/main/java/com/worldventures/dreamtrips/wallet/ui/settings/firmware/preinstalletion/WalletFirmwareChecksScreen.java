package com.worldventures.dreamtrips.wallet.ui.settings.firmware.preinstalletion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.Button;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletCheckWidget;

import butterknife.InjectView;
import butterknife.OnClick;

public class WalletFirmwareChecksScreen extends WalletLinearLayout<WalletFirmwareChecksPresenter.Screen, WalletFirmwareChecksPresenter, WalletFirmwareChecksPath>
      implements WalletFirmwareChecksPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.check_widget_charge) WalletCheckWidget checkWidgetCharge;
   @InjectView(R.id.check_widget_connection) WalletCheckWidget checkWidgetConnection;
   @InjectView(R.id.check_widget_bluetooth) WalletCheckWidget checkWidgetBluetooth;
   @InjectView(R.id.install) Button installButton;

   public WalletFirmwareChecksScreen(Context context) {
      super(context);
   }

   public WalletFirmwareChecksScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public WalletFirmwareChecksPresenter createPresenter() {
      return new WalletFirmwareChecksPresenter(getContext(), getInjector(), getPath().firmwareFilePath());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(view -> presenter.goBack());
   }

   @OnClick(R.id.install_later)
   protected void installLaterClick() {
      presenter.installLater();
   }

   @OnClick(R.id.install)
   protected void installClick() {
      presenter.install();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
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
      checkWidgetCharge.setChecked(charged);
   }

   @Override
   public void connectionStatusVisible(boolean isVisible) {
      checkWidgetConnection.setVisibility(isVisible ? VISIBLE : GONE);
   }

   @Override
   public void chargedStatusVisible(boolean isVisible) {
      checkWidgetCharge.setVisibility(isVisible ? VISIBLE : GONE);
   }

   @Override
   public void installButtonEnabled(boolean enabled) {
      installButton.setEnabled(enabled);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}
