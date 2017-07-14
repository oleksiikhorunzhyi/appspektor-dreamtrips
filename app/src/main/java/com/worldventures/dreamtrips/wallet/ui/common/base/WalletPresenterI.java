package com.worldventures.dreamtrips.wallet.ui.common.base;


import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public interface WalletPresenterI<V extends WalletScreen> {

   void attachView(V view);

   void detachView(boolean retainInstance);

}
