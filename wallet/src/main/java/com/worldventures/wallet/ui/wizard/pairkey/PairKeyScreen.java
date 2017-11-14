package com.worldventures.wallet.ui.wizard.pairkey;

import com.worldventures.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface PairKeyScreen extends WalletScreen, PairView {
   OperationView<CreateAndConnectToCardCommand> provideOperationCreateAndConnect();

   ProvisioningMode getProvisionMode();

   String getBarcode();

   void nextButtonEnable(boolean enable);
}