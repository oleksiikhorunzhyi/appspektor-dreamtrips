package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface NewCardPowerOnPresenter extends WalletPresenterI<NewCardPowerOnScreen> {

   void goBack();

   void navigateNext();

   void cantTurnOnSmartCard();

   void unassignCardOnBackend();

}
