package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface PairKeyPresenter extends WalletPresenterI<PairKeyScreen> {

   void tryToPairAndConnectSmartCard();

   void goBack();

}
