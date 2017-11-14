package com.worldventures.wallet.ui.wizard.termsandconditionals;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WizardTermsPresenter extends WalletPresenter<WizardTermsScreen> {

   void onBack();

   void loadTerms();

   void acceptTermsPressed();

}
