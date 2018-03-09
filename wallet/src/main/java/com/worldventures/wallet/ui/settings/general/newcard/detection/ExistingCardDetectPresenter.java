package com.worldventures.wallet.ui.settings.general.newcard.detection;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface ExistingCardDetectPresenter extends WalletPresenter<ExistingCardDetectScreen> {

   void goBack();

   void fetchSmartCardId();

   void fetchSmartCardConnection();

   void cardAvailable();

   void unassignWithoutCard();

   void unassignCard();

   void unassignCardConfirmed(String smartCardId);

   void unassignWithoutCardConfirmed(String smartCardId);
}
