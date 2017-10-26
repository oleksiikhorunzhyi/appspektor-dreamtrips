package com.worldventures.wallet.ui.settings.general.firmware.download;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WalletDownloadFirmwarePresenter extends WalletPresenter<WalletDownloadFirmwareScreen> {

   void cancelDownload();

   void downloadFirmware();

   void goBack();
}
