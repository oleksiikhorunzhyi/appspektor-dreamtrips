package com.worldventures.dreamtrips.wallet.ui.common.activity;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.RxLifecycleView;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public interface WalletActivityView extends WalletScreen, RxLifecycleView {

   void openBluetoothSettings();
}
