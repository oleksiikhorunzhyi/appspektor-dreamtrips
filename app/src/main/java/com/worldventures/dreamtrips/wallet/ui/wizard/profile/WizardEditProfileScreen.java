package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfilePhotoView;

import org.jetbrains.annotations.Nullable;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WizardEditProfileScreen extends WalletScreen, WalletProfilePhotoView {

   OperationView<SetupUserDataCommand> provideOperationView();

   void setProfile(ProfileViewModel model);

   ProfileViewModel getProfile();

   void showConfirmationDialog(ProfileViewModel profileViewModel);

   @Nullable
   ProvisioningMode getProvisionMode();
}
