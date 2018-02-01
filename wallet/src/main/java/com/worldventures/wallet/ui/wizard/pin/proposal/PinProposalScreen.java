package com.worldventures.wallet.ui.wizard.pin.proposal;

import android.view.View;

import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

public interface PinProposalScreen extends WalletScreen {

   void setLabel();

   void setLabelWithCardName(String cardNickName);

   void setToolbarTitle(int toolbarTitleResId);

   void setSkipButtonLabel(int skipButtonResId);

   void preparePinOptionalDialog(PinProposalDelegate pinProposalDelegate);

   void showSkipPinDialog();

   void hideSkipPinDialog();

   void disableToolbarNavigation();

   void setupToolbarNavigation();

   PinProposalAction getPinProposalAction();

   String getCardNickName();

   View getView();
}
