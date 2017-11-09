package com.worldventures.wallet.ui.settings.general.firmware.reset.pair;

import android.support.annotation.StringRes;

import com.worldventures.wallet.service.firmware.command.ConnectForFirmwareUpdate;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface ForcePairKeyScreen extends WalletScreen {

   void showError(@StringRes int messageId);

   OperationView<ConnectForFirmwareUpdate> provideOperationConnect();
}