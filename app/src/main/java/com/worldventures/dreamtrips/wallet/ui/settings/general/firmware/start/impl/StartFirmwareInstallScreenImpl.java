package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.impl;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.StartFirmwareInstallPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.StartFirmwareInstallScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.functions.Action1;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class StartFirmwareInstallScreenImpl extends WalletBaseController<StartFirmwareInstallScreen, StartFirmwareInstallPresenter> implements StartFirmwareInstallScreen {
   @InjectView(R.id.progress) WalletProgressWidget progressView;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   @Inject StartFirmwareInstallPresenter presenter;

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_start_firmware_install, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return true;
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return this;
   }

   @Override
   public void showProgress(@Nullable String text) {
      progressView.setVisibility(VISIBLE);
      progressView.start();
   }

   @Override
   public void hideProgress() {
      progressView.stop();
      progressView.setVisibility(GONE);
   }

   @Override
   public Context context() {
      return getContext();
   }

   @Override
   public void showError(String message, @Nullable Action1<Void> action) {
      new MaterialDialog.Builder(getContext())
            .content(message)
            .positiveText(R.string.wallet_firmware_install_error_retry_action)
            .negativeText(R.string.wallet_firmware_install_error_cancel_action)
            .onPositive((dialog, which) -> {
               if (action != null) action.call(null);
            })
            .onNegative((dialog, which) -> getPresenter().finish())
            .show();
   }

   @Override
   public StartFirmwareInstallPresenter getPresenter() {
      return presenter;
   }
}
