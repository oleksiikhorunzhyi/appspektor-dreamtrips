package com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WizardTermsPresenter extends WalletPresenterI<WizardTermsScreen> {

   void onBack();

   void loadTerms();

   void acceptTermsPressed();

}
