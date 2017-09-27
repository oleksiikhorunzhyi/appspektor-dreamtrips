package com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WizardTermsPresenter extends WalletPresenter<WizardTermsScreen> {

   void onBack();

   void loadTerms();

   void acceptTermsPressed();

}
