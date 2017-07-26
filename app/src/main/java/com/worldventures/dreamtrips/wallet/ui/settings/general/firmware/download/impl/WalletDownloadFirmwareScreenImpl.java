package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.impl;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.WalletDownloadFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.WalletDownloadFirmwareScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;

public class WalletDownloadFirmwareScreenImpl extends WalletBaseController<WalletDownloadFirmwareScreen, WalletDownloadFirmwarePresenter> implements WalletDownloadFirmwareScreen {
   @InjectView(R.id.firmware_download_progress) WalletProgressWidget downloadProgress;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   @Inject WalletDownloadFirmwarePresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_download_firmware, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @OnClick(R.id.wallet_cancel_download)
   void cancelDownload() {
      getPresenter().cancelDownload();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return this;
   }

   @Override
   public void showProgress(@Nullable String text) {
      downloadProgress.start();
   }

   @Override
   public void hideProgress() {
      //needless
   }

   @Override
   public void showError(String msg, @Nullable Action1<Void> action) {
      Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT)
            .setCallback(new Snackbar.Callback() {
               @Override
               public void onDismissed(Snackbar snackbar, int event) {
                  if (action != null) action.call(null);
               }
            }).show();
   }

   @Override
   public Context context() {
      return getContext();
   }

   @Override
   public WalletDownloadFirmwarePresenter getPresenter() {
      return presenter;
   }
}
