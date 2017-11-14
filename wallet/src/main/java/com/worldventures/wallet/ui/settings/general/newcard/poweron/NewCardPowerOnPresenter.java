package com.worldventures.wallet.ui.settings.general.newcard.poweron;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface NewCardPowerOnPresenter extends WalletPresenter<NewCardPowerOnScreen> {

   void goBack();

   void navigateNext();

   void cantTurnOnSmartCard();

   void unassignCardOnBackend();

}
