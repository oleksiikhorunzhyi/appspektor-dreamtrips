package com.worldventures.wallet.ui.wizard.unassign;

import com.worldventures.wallet.service.command.wizard.ReAssignCardCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface ExistingDeviceDetectScreen extends WalletScreen {

   OperationView<ReAssignCardCommand> provideOperationView();

   void setSmartCardId(String scId);

   void showConfirmDialog(String scId);

   String getSmartCardId();
}