package com.worldventures.wallet.ui.wizard.records.sync;

import com.worldventures.wallet.ui.common.base.WalletPresenter;


public interface SyncRecordsPresenter extends WalletPresenter<SyncRecordsScreen> {

   void retrySync();

   void navigateToWallet();

}
