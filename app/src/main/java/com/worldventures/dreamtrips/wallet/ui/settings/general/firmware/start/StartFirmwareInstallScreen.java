package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start;

import com.worldventures.dreamtrips.wallet.service.firmware.command.PrepareForUpdateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface StartFirmwareInstallScreen extends WalletScreen {

   OperationView<PrepareForUpdateCommand> provideOperationPrepareForUpdate();

}