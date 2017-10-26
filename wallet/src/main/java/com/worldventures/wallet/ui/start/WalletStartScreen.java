package com.worldventures.wallet.ui.start;


import com.worldventures.wallet.service.command.wizard.FetchAssociatedSmartCardCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletStartScreen extends WalletScreen {

   OperationView<FetchAssociatedSmartCardCommand> provideOperationView();

}
