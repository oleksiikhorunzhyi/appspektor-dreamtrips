package com.worldventures.wallet.ui.settings.general.firmware.reset.pair;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface ForcePairKeyPresenter extends WalletPresenter<ForcePairKeyScreen> {

   void goBack();

   void tryToPairAndConnectSmartCard();

}
