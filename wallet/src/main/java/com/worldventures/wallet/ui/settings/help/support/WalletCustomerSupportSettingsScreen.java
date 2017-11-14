package com.worldventures.wallet.ui.settings.help.support;

import com.worldventures.wallet.domain.entity.settings.customer_support.Contact;
import com.worldventures.wallet.service.command.settings.help.GetCustomerSupportContactCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletCustomerSupportSettingsScreen extends WalletScreen {

   void bindData(Contact contact);

   OperationView<GetCustomerSupportContactCommand> provideOperationView();
}
