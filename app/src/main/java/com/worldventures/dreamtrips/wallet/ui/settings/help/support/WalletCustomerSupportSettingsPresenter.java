package com.worldventures.dreamtrips.wallet.ui.settings.help.support;

import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WalletCustomerSupportSettingsPresenter extends WalletPresenter<WalletCustomerSupportSettingsScreen> {

   void goBack();

   void dialPhoneNumber(String phoneNumber);

   void openCustomerSupportFeedbackScreen();

   void fetchCustomerSupportContact();

   HttpErrorHandlingUtil httpErrorHandlingUtil();
}
