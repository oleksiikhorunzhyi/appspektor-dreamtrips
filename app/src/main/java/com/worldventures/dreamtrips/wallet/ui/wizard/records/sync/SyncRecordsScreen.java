package com.worldventures.dreamtrips.wallet.ui.wizard.records.sync;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.SyncAction;

public interface SyncRecordsScreen extends WalletScreen, SyncView {

   SyncAction getSyncAction();
}
