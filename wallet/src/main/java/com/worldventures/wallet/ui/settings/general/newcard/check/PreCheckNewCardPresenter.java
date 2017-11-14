package com.worldventures.wallet.ui.settings.general.newcard.check;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface PreCheckNewCardPresenter extends WalletPresenter<PreCheckNewCardScreen> {

   void goBack();

   void navigateNext();

   void prepareContinueAddCard();

}
