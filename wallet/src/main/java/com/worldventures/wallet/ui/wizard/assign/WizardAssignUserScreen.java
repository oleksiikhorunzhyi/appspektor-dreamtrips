package com.worldventures.wallet.ui.wizard.assign;

import android.view.View;

import com.worldventures.wallet.service.command.wizard.WizardCompleteCommand;
import com.worldventures.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WizardAssignUserScreen extends WalletScreen {

   OperationView<WizardCompleteCommand> provideOperationView();

   ProvisioningMode getProvisionMode();

   View getView();
}
