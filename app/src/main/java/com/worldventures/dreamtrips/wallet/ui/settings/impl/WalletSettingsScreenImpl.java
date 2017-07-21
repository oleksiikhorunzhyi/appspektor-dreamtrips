package com.worldventures.dreamtrips.wallet.ui.settings.impl;


import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeView;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsScreen;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

public class WalletSettingsScreenImpl extends WalletBaseController<WalletSettingsScreen, WalletSettingsPresenter>
      implements WalletSettingsScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.status) TextView status;
   @InjectView(R.id.badgeFirmwareUpdates) BadgeView badgeFirmwareUpdates;
   @InjectViews({R.id.item_help, R.id.item_security}) List<View> toggleableItems;

   @Inject WalletSettingsPresenter presenter;

   public WalletSettingsScreenImpl() {
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      badgeFirmwareUpdates.hide();
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   protected void onNavigationClick() {
      getPresenter().goBack();
   }

   @OnClick(R.id.item_general)
   void onClickGeneral() {
      getPresenter().openGeneralScreen();
   }

   @OnClick(R.id.item_security)
   void onClickSecurity() {
      getPresenter().openSecurityScreen();
   }

   @OnClick(R.id.item_help)
   void onClickHelp() {
      getPresenter().openHelpScreen();
   }

   @Override
   public void smartCardGeneralStatus(@Nullable SmartCardFirmware version, int batteryLevel, Date lastSync) {
      final StringBuilder builder = new StringBuilder();
      if (version != null) {
         builder.append(getString(R.string.wallet_card_settings_version, version.nordicAppVersion())).append("\n");
      }
      builder.append(getString(R.string.wallet_card_settings_battery_level, batteryLevel)).append("\n")
      // TODO: 5/5/17 Implement this ffs
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
   public List<View> getToggleableItems() {
      return toggleableItems;
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(getView());
   }

   @Override
   public WalletSettingsPresenter getPresenter() {
      return presenter;
   }
}
