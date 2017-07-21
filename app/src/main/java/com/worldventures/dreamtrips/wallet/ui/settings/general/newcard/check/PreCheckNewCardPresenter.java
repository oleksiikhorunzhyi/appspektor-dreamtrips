package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface PreCheckNewCardPresenter extends WalletPresenterI<PreCheckNewCardScreen> {

   void goBack();

   void navigateNext();

   void prepareContinueAddCard();

}
