package com.worldventures.dreamtrips.wallet.ui.settings.security.clear.common.base;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WalletBaseClearDelayPresenter<S extends WalletBaseClearDelayScreen> extends WalletPresenter<S> {

   void goBack();

   void onTimeSelected(long time);

   void trackChangedDelay();
}
