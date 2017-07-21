package com.worldventures.dreamtrips.wallet.ui.wizard.records.sync;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;


public interface SyncRecordsPresenter extends WalletPresenterI<SyncRecordsScreen> {

   void retrySync();

   void navigateToWallet();

}
