package com.worldventures.dreamtrips.wallet.ui.start;


import com.worldventures.dreamtrips.wallet.service.command.wizard.FetchAssociatedSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletStartScreen extends WalletScreen {

   OperationView<FetchAssociatedSmartCardCommand> provideOperationView();

}
