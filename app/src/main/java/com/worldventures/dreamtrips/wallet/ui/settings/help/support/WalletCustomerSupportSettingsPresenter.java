package com.worldventures.dreamtrips.wallet.ui.settings.help.support;

import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WalletCustomerSupportSettingsPresenter extends WalletPresenterI<WalletCustomerSupportSettingsScreen> {

   void goBack();

   void dialPhoneNumber(String phoneNumber);

   void openCustomerSupportFeedbackScreen();

   void fetchCustomerSupportContact();

   HttpErrorHandlingUtil httpErrorHandlingUtil();
}
