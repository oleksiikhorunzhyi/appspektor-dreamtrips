package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public interface WalletFirmwareChecksScreen extends WalletScreen {

   void bluetoothEnabled(boolean enabled);

   void cardConnected(boolean connected);

   void cardCharged(boolean charged);

   void connectionStatusVisible(boolean isVisible);

   void chargedStatusVisible(boolean isVisible);

   void installButtonEnabled(boolean enabled);

   void cardIsInCharger(boolean enabled);

   void cardIsInChargerCheckVisible(boolean isVisible);

}
