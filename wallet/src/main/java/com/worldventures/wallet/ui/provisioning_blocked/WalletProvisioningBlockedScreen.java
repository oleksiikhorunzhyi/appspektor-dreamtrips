package com.worldventures.wallet.ui.provisioning_blocked;

import com.worldventures.wallet.service.command.GetCompatibleDevicesCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.provisioning_blocked.holder.SupportedDevicesListModel;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletProvisioningBlockedScreen extends WalletScreen {
   void onSupportedDevicesLoaded(SupportedDevicesListModel devices);

   OperationView<GetCompatibleDevicesCommand> provideOperationGetCompatibleDevices();
}
