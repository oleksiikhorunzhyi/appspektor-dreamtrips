package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface PairKeyPresenter extends WalletPresenter<PairKeyScreen> {

   void tryToPairAndConnectSmartCard();

   void goBack();

}
