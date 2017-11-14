package com.worldventures.wallet.ui.wizard.records.sync;

import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.wizard.records.SyncAction;

public interface SyncRecordsScreen extends WalletScreen, SyncView {

   SyncAction getSyncAction();
}
