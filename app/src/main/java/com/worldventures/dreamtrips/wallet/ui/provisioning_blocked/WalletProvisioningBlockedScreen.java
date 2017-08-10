package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked;

import com.worldventures.dreamtrips.wallet.service.command.GetCompatibleDevicesCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.SupportedDevicesListModel;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletProvisioningBlockedScreen extends WalletScreen {
   void onSupportedDevicesLoaded(SupportedDevicesListModel devices);

   OperationView<GetCompatibleDevicesCommand> provideOperationGetCompatibleDevices();
}
