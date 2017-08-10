package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.impl;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.service.firmware.command.DownloadFirmwareCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.WalletProgressView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.WalletDownloadFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.WalletDownloadFirmwareScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WalletDownloadFirmwareScreenImpl extends WalletBaseController<WalletDownloadFirmwareScreen, WalletDownloadFirmwarePresenter> implements WalletDownloadFirmwareScreen {
   @InjectView(R.id.firmware_download_progress) WalletProgressWidget downloadProgress;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   @Inject WalletDownloadFirmwarePresenter presenter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

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
   public WalletDownloadFirmwarePresenter getPresenter() {
      return presenter;
   }

   @Override
   public OperationView<DownloadFirmwareCommand> provideOperationDownload() {
      return new ComposableOperationView<>(
            new WalletProgressView<>(downloadProgress),
            ErrorViewFactory.<DownloadFirmwareCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(), httpErrorHandlingUtil,
                        cmd -> getPresenter().downloadFirmware(),
                        cmd -> getPresenter().goBack()))
                  .addProvider(new SimpleDialogErrorViewProvider<>(
                        getContext(),
                        IllegalStateException.class,
                        R.string.wallet_firmware_download_text,
                        cmd -> getPresenter().goBack()))
                  .build()
      );
   }
}
