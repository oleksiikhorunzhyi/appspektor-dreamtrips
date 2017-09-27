package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetView;

public interface PreCheckNewCardScreen extends WalletScreen, FactoryResetView {

   void showAddCardContinueDialog(String scId);

   void nextButtonEnabled(boolean enable);

   void bluetoothEnable(boolean enabled);

   void cardConnected(boolean enabled);

   void setVisiblePowerSmartCardWidget(boolean visible);
}
