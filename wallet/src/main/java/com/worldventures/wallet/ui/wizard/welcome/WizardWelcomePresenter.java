package com.worldventures.wallet.ui.wizard.welcome;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WizardWelcomePresenter extends WalletPresenter<WizardWelcomeScreen> {

   void backButtonClicked();

   void setupCardClicked();
}
