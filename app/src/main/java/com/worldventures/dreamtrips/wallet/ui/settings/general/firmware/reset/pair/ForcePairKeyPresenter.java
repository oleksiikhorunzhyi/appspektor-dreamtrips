package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface ForcePairKeyPresenter extends WalletPresenter<ForcePairKeyScreen> {

   void goBack();

   void tryToPairAndConnectSmartCard();

}
