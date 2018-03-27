package com.worldventures.wallet.ui.settings.general.firmware.download;

import com.worldventures.wallet.service.firmware.command.DownloadFirmwareCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletDownloadFirmwareScreen extends WalletScreen {

   OperationView<DownloadFirmwareCommand> provideOperationDownload();
}
