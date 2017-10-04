package com.worldventures.dreamtrips.wallet.ui.common.activity;

import rx.Observable;

public interface WalletActivityPresenter {

   void attachView(WalletActivityView view);

   void detachView();

   void logout();

   void bindToBluetooth(Observable<Void> terminateObservable);
}
