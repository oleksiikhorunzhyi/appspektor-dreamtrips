package com.worldventures.dreamtrips.wallet.ui.wizard.assign;

import android.view.View;

import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCompleteCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WizardAssignUserScreen extends WalletScreen {

   OperationView<WizardCompleteCommand> provideOperationView();

   ProvisioningMode getProvisionMode();

   View getView();
}
