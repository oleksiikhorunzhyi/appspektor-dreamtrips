package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download;


import com.worldventures.dreamtrips.wallet.service.firmware.command.DownloadFirmwareCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletDownloadFirmwareScreen extends WalletScreen {

   OperationView<DownloadFirmwareCommand> provideOperationDownload();
}
