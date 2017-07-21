package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface ForcePairKeyPresenter extends WalletPresenterI<ForcePairKeyScreen> {

   void goBack();

   void tryToPairAndConnectSmartCard();

}
