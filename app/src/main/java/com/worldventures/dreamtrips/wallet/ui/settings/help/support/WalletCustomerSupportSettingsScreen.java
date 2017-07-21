package com.worldventures.dreamtrips.wallet.ui.settings.help.support;

import android.content.Context;

import com.worldventures.dreamtrips.wallet.domain.entity.settings.customer_support.Contact;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.GetCustomerSupportContactCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletCustomerSupportSettingsScreen extends WalletScreen {

   void bindData(Contact contact);

   OperationView<GetCustomerSupportContactCommand> provideOperationView();

   Context getViewContext();
}
