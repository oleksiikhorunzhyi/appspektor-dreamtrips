package com.worldventures.dreamtrips.wallet.ui.wizard.checking;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public interface WizardCheckingScreen extends WalletScreen {

   void networkAvailable(boolean available);

   void bluetoothEnable(boolean enable);

   void bluetoothDoesNotSupported();

   void buttonEnable(boolean enable);
}
