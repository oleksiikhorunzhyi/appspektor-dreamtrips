package com.worldventures.dreamtrips.wallet.ui.settings.help;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WalletHelpSettingsPresenter extends WalletPresenter<WalletHelpSettingsScreen> {

   void goBack();

   void openDocumentsScreen();

   void openPaymentFeedbackScreen();
   void openVideoScreen();

   void handleVariantFeedback();

   void openCustomerSupportScreen();

   void openOtherFeedbackScreen();
}
