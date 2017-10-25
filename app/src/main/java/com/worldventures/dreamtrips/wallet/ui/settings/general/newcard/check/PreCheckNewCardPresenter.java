package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface PreCheckNewCardPresenter extends WalletPresenter<PreCheckNewCardScreen> {

   void goBack();

   void navigateNext();

   void prepareContinueAddCard();

}
