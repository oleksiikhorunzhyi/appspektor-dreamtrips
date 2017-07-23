package com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;


public interface PinProposalPresenter extends WalletPresenter<PinProposalScreen> {

   void goBack();

   void handleCreatePin();

   void handleSkipPinCreation();

   void remindMeLater();

   void cancelDialog();

   void dontShowAgain();

}
