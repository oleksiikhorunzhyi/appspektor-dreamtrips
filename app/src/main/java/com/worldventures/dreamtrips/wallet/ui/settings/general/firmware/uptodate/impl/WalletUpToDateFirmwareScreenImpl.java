package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.uptodate.impl;


import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.uptodate.WalletUpToDateFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.uptodate.WalletUpToDateFirmwareScreen;

import javax.inject.Inject;

import butterknife.InjectView;

public class WalletUpToDateFirmwareScreenImpl extends WalletBaseController<WalletUpToDateFirmwareScreen, WalletUpToDateFirmwarePresenter> implements WalletUpToDateFirmwareScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.version) TextView versionView;

   @Inject WalletUpToDateFirmwarePresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
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
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(getView());
   }

   @Override
   public void version(@Nullable SmartCardFirmware version) {
      versionView.setText(version == null ? "" : getString(R.string.wallet_settings_version, version.nordicAppVersion()));
   }

   @Override
   public WalletUpToDateFirmwarePresenter getPresenter() {
      return presenter;
   }
}
