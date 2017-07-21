package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.SupportedDevicesListModel;

public interface WalletProvisioningBlockedScreen extends WalletScreen {
   void onSupportedDevicesLoaded(SupportedDevicesListModel devices);
}
