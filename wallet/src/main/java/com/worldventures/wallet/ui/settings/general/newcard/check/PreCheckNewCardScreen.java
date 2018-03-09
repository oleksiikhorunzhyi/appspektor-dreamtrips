package com.worldventures.wallet.ui.settings.general.newcard.check;

import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetView;

public interface PreCheckNewCardScreen extends WalletScreen, FactoryResetView {

   void showAddCardContinueDialog(String scId);

   void nextButtonEnabled(boolean enable);

   void bluetoothEnable(boolean enabled);

   void cardConnected(boolean enabled);

   void setVisiblePowerSmartCardWidget(boolean visible);
}
