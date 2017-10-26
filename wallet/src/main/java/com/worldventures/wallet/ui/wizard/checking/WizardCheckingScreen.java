package com.worldventures.wallet.ui.wizard.checking;

import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

public interface WizardCheckingScreen extends WalletScreen {

   void networkAvailable(boolean available);

   void bluetoothEnable(boolean enable);

   void bluetoothDoesNotSupported();

   void buttonEnable(boolean enable);
}
