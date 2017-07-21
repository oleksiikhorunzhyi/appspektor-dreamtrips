package com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface EnterPinPresenter extends WalletPresenterI<EnterPinScreen> {

   void goBack();

   void retry();

   void cancelSetupPIN();
}
