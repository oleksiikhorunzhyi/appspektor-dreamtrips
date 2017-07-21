package com.worldventures.dreamtrips.wallet.ui.settings.security.removecards;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WalletAutoClearCardsPresenter extends WalletPresenterI<WalletAutoClearCardsScreen> {

   void goBack();

   void onTimeSelected(long time);
}
