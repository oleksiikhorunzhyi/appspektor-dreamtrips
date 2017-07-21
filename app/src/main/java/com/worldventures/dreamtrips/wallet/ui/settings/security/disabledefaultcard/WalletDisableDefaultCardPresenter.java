package com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WalletDisableDefaultCardPresenter extends WalletPresenterI<WalletDisableDefaultCardScreen> {

   void goBack();

   void onTimeSelected(long selectedTime);
}
