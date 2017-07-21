package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.detection;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface ExistingCardDetectPresenter extends WalletPresenterI<ExistingCardDetectScreen> {

   void goBack();

   void navigateToPowerOn();

   void prepareUnassignCardOnBackend();

   void prepareUnassignCard();

   void unassignCard();

   void unassignCardOnBackend();

}
