package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public interface ForcePairKeyScreen extends WalletScreen {
   void showError(@StringRes int messageId);
}