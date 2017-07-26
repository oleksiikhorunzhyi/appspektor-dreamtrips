package com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WalletDisableDefaultCardPresenter extends WalletPresenter<WalletDisableDefaultCardScreen> {

   void goBack();

   void onTimeSelected(long selectedTime);

   void trackChangedDelay();
}
