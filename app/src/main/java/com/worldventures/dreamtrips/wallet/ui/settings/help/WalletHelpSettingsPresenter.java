package com.worldventures.dreamtrips.wallet.ui.settings.help;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WalletHelpSettingsPresenter extends WalletPresenterI<WalletHelpSettingsScreen> {

   void goBack();

   void openDocumentsScreen();

   void openPaymentFeedbackScreen();
   void openVideoScreen();

   void handleVariantFeedback();

   void openCustomerSupportScreen();

   void openOtherFeedbackScreen();
}
