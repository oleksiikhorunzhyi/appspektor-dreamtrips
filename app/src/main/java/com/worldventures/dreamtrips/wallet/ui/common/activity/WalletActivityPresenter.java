package com.worldventures.dreamtrips.wallet.ui.common.activity;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

import rx.Observable;

public interface WalletActivityPresenter extends WalletPresenter<WalletActivityView> {

   void logout();

   void bindToBluetooth(Observable<Void> terminateObservable);
}
