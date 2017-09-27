package com.worldventures.dreamtrips.wallet.ui.wizard.records.sync;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;


public interface SyncRecordsPresenter extends WalletPresenter<SyncRecordsScreen> {

   void retrySync();

   void navigateToWallet();

}
