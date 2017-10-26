package com.worldventures.wallet.ui.wizard.pin.success;

import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.wizard.pin.Action;

public interface PinSetSuccessScreen extends WalletScreen {

   void showMode(Action mode);

   Action getPinAction();

}
