package com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface EnterPinPresenter extends WalletPresenter<EnterPinScreen> {

   void goBack();

   void retry();

   void cancelSetupPIN();
}
