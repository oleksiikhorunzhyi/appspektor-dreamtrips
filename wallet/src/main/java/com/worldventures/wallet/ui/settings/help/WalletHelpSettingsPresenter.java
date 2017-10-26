package com.worldventures.wallet.ui.settings.help;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WalletHelpSettingsPresenter extends WalletPresenter<WalletHelpSettingsScreen> {

   void goBack();

   void openDocumentsScreen();

   void openPaymentFeedbackScreen();
   void openVideoScreen();

   void handleVariantFeedback();

   void openCustomerSupportScreen();

   void openOtherFeedbackScreen();
}
