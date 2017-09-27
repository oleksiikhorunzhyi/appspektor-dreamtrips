package com.worldventures.dreamtrips.wallet.ui.wizard.pin.success;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;

public interface PinSetSuccessScreen extends WalletScreen {

   void showMode(Action mode);

   Action getPinAction();

}
