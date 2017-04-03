package com.worldventures.dreamtrips.wallet.ui.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeView;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import java.util.Date;

import butterknife.InjectView;
import butterknife.OnClick;

public class WalletSettingsScreen extends WalletLinearLayout<WalletSettingsPresenter.Screen, WalletSettingsPresenter, WalletSettingsPath> implements WalletSettingsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.status) TextView status;
   @InjectView(R.id.badgeFirmwareUpdates) BadgeView badgeFirmwareUpdates;

   public WalletSettingsScreen(Context context) {
      this(context, null);
   }

   public WalletSettingsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      badgeFirmwareUpdates.hide();
   }

   @NonNull
   @Override
   public WalletSettingsPresenter createPresenter() {
      return new WalletSettingsPresenter(getContext(), getInjector());
   }

   protected void onNavigationClick() {
      presenter.goBack();
   }

   @OnClick(R.id.item_general)
   void onClickGeneral() {
      presenter.openGeneralScreen();
   }

   @OnClick(R.id.item_security)
   void onClickSecurity() {
      presenter.openSecurityScreen();
   }

   @OnClick(R.id.item_help)
   void onClickHelp() {
      presenter.openHelpScreen();
   }

   @Override
   public void smartCardGeneralStatus(@Nullable SmartCardFirmware version, int batteryLevel, Date lastSync) {
      final StringBuilder builder = new StringBuilder();
      if (version != null) {
         builder.append(getString(R.string.wallet_card_settings_version, version.nordicAppVersion())).append("\n");
      }
      builder.append(getString(R.string.wallet_card_settings_battery_level, batteryLevel)).append("\n")
      //                  .append(getString(R.string.wallet_card_settings_last_sync, formattedLastSync))
      ;

      status.setText(builder.toString());
   }

   @Override
   public void firmwareUpdateCount(int count) {
      badgeFirmwareUpdates.setText(String.valueOf(count));
      if (count > 0) badgeFirmwareUpdates.show(true);
      else badgeFirmwareUpdates.hide(true);
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

}
