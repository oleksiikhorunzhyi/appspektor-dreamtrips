package com.worldventures.wallet.ui.settings.security.clear.common.base;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WalletBaseClearDelayPresenter<S extends WalletBaseClearDelayScreen> extends WalletPresenter<S> {

   void goBack();

   void onTimeSelected(long time);

   void trackChangedDelay();
}
