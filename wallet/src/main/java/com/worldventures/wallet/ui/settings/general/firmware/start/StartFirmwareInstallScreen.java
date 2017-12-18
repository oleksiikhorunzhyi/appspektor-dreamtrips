package com.worldventures.wallet.ui.settings.general.firmware.start;

import com.worldventures.wallet.service.firmware.command.PrepareForUpdateCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface StartFirmwareInstallScreen extends WalletScreen {

   OperationView<PrepareForUpdateCommand> provideOperationPrepareForUpdate();

}