package com.worldventures.wallet.ui.wizard.pin.proposal;

import com.worldventures.wallet.ui.common.base.WalletPresenter;


public interface PinProposalPresenter extends WalletPresenter<PinProposalScreen> {

   void goBack();

   void handleCreatePin();

   void handleSkipPinCreation();

   void remindMeLater();

   void cancelDialog();

   void dontShowAgain();

}
