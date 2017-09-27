package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WalletDownloadFirmwarePresenter extends WalletPresenter<WalletDownloadFirmwareScreen> {

   void cancelDownload();

   void downloadFirmware();

   void goBack();
}
