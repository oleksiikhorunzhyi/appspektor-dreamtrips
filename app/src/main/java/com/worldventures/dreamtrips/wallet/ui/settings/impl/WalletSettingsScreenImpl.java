package com.worldventures.dreamtrips.wallet.ui.settings.impl;


import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.core.ui.view.custom.BadgeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsScreen;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class WalletSettingsScreenImpl extends WalletBaseController<WalletSettingsScreen, WalletSettingsPresenter>
      implements WalletSettingsScreen {

   private TextView status;
   private BadgeView badgeFirmwareUpdates;
   private List<View> toggleableItems;

   @Inject WalletSettingsPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      status = view.findViewById(R.id.status);
      badgeFirmwareUpdates = view.findViewById(R.id.badgeFirmwareUpdates);
      badgeFirmwareUpdates.hide();
      final View itemGeneralView = view.findViewById(R.id.item_general);
      itemGeneralView.setOnClickListener(general -> getPresenter().openGeneralScreen());
      final View itemHelpView = view.findViewById(R.id.item_help);
      itemHelpView.setOnClickListener(help -> getPresenter().openHelpScreen());
      final View itemSecurityView = view.findViewById(R.id.item_security);
      itemSecurityView.setOnClickListener(security -> getPresenter().openSecurityScreen());
      toggleableItems = Arrays.asList(itemHelpView, itemSecurityView);
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

   @Override
   public void smartCardGeneralStatus(@Nullable SmartCardFirmware version, int batteryLevel, Date lastSync) {
      final StringBuilder builder = new StringBuilder();
      if (version != null) {
         builder.append(getString(R.string.wallet_card_settings_version, version.nordicAppVersion())).append("\n");
      }
      builder.append(getString(R.string.wallet_card_settings_battery_level, batteryLevel)).append("\n");
      // TODO: 5/5/17 Implement this ffs
      //                  .append(getString(R.string.wallet_card_settings_last_sync, formattedLastSync));

      status.setText(builder.toString());
   }

   @Override
   public void firmwareUpdateCount(int count) {
      badgeFirmwareUpdates.setText(String.valueOf(count));
      if (count > 0) {
         badgeFirmwareUpdates.show(true);
      } else {
         badgeFirmwareUpdates.hide(true);
      }
   }

   @Override
   public List<View> getToggleableItems() {
      return toggleableItems;
   }

   @Override
   public WalletSettingsPresenter getPresenter() {
      return presenter;
   }
}
