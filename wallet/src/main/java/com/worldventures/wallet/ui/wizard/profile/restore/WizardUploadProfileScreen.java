package com.worldventures.wallet.ui.wizard.profile.restore;

import android.support.annotation.Nullable;

import com.worldventures.wallet.service.command.SetupUserDataCommand;
import com.worldventures.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WizardUploadProfileScreen extends WalletScreen {

   OperationView<SetupUserDataCommand> provideOperationSetupUserData();

   void showRetryDialog();

   @Nullable
   ProvisioningMode getProvisionMode();
}
