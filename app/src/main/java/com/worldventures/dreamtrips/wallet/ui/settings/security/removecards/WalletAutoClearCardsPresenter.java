package com.worldventures.dreamtrips.wallet.ui.settings.security.removecards;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WalletAutoClearCardsPresenter extends WalletPresenter<WalletAutoClearCardsScreen> {

   void goBack();

   void onTimeSelected(long time);
}
