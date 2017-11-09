package com.worldventures.wallet.ui.settings.help.support;

import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WalletCustomerSupportSettingsPresenter extends WalletPresenter<WalletCustomerSupportSettingsScreen> {

   void goBack();

   void dialPhoneNumber(String phoneNumber);

   void openCustomerSupportFeedbackScreen();

   void fetchCustomerSupportContact();

   HttpErrorHandlingUtil httpErrorHandlingUtil();
}
