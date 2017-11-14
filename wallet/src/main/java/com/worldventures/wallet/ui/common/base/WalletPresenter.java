package com.worldventures.wallet.ui.common.base;


import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

public interface WalletPresenter<V extends WalletScreen> {

   void attachView(V view);

   void detachView(boolean retainInstance);

}
