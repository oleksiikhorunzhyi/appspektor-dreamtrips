package com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore;

import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WizardUploadProfileScreen extends WalletScreen {

   OperationView<SetupUserDataCommand> provideOperationSetupUserData();

   void showRetryDialog();

}
