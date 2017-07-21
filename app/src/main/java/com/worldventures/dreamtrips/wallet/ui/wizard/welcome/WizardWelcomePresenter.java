package com.worldventures.dreamtrips.wallet.ui.wizard.welcome;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WizardWelcomePresenter extends WalletPresenterI<WizardWelcomeScreen> {

   void backButtonClicked();

   void setupCardClicked();
}
