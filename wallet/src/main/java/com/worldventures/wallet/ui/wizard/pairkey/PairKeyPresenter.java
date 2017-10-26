package com.worldventures.wallet.ui.wizard.pairkey;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface PairKeyPresenter extends WalletPresenter<PairKeyScreen> {

   void tryToPairAndConnectSmartCard();

   void goBack();

}
