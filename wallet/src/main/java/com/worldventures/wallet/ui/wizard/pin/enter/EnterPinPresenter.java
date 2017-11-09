package com.worldventures.wallet.ui.wizard.pin.enter;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface EnterPinPresenter extends WalletPresenter<EnterPinScreen> {

   void goBack();

   void retry();

   void cancelSetupPIN();
}
