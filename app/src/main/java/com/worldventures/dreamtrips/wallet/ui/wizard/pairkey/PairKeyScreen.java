package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey;

import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface PairKeyScreen extends WalletScreen, PairView {
   OperationView<CreateAndConnectToCardCommand> provideOperationCreateAndConnect();

   ProvisioningMode getProvisionMode();

   String getBarcode();
}