package com.worldventures.wallet.ui.settings.general.firmware.uptodate.impl;


import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.wallet.R;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.settings.general.firmware.uptodate.WalletUpToDateFirmwarePresenter;
import com.worldventures.wallet.ui.settings.general.firmware.uptodate.WalletUpToDateFirmwareScreen;

import javax.inject.Inject;

public class WalletUpToDateFirmwareScreenImpl extends WalletBaseController<WalletUpToDateFirmwareScreen, WalletUpToDateFirmwarePresenter> implements WalletUpToDateFirmwareScreen {

   private TextView versionView;

   @Inject WalletUpToDateFirmwarePresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      versionView = view.findViewById(R.id.version);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_up_to_date_firmware, viewGroup, false);
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
   public void version(@Nullable SmartCardFirmware version) {
      versionView.setText(version == null ? "" : getString(R.string.wallet_settings_version, version.getNordicAppVersion()));
   }

   @Override
   public WalletUpToDateFirmwarePresenter getPresenter() {
      return presenter;
   }
}
